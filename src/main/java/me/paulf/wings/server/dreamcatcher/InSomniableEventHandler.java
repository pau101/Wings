package me.paulf.wings.server.dreamcatcher;

import me.paulf.wings.WingsMod;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class InSomniableEventHandler {
	private InSomniableEventHandler() {}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onLeftClickBlock(final PlayerInteractEvent.LeftClickBlock event) {
		final EntityPlayer player = event.getEntityPlayer();
		if (player instanceof EntityPlayerMP) {
			final World world = event.getWorld();
			final BlockPos pos = event.getPos();
			final Block block = world.getBlockState(pos).getBlock();
			if (block == Blocks.NOTEBLOCK && canEdit((EntityPlayerMP) player, block)) {
				final TileEntity entity = world.getTileEntity(pos);
				final Playable playable;
				if (entity instanceof TileEntityNote && (playable = InSomniableCapability.getPlayable((TileEntityNote) entity)) != null) {
					playable.setPlayer(player.getUniqueID());
				}
			}
		}
	}

	private static boolean canEdit(final EntityPlayerMP player, final Block block) {
		final GameType gameType = player.interactionManager.getGameType();
		if (!gameType.hasLimitedInteractions()) {
			return true;
		}
		if (gameType == GameType.SPECTATOR) {
			return false;
		}
		if (player.isAllowEdit()) {
			return true;
		}
		final ItemStack stack = player.getHeldItemMainhand();
		return !stack.isEmpty() && stack.canDestroy(block);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onNoteBlockPlay(final NoteBlockEvent.Play event) {
		final World world = event.getWorld();
		if (!world.isRemote) {
			final TileEntity entity = world.getTileEntity(event.getPos());
			final Playable playable;
			if (entity instanceof TileEntityNote && (playable = InSomniableCapability.getPlayable((TileEntityNote) entity)) != null) {
				playable.ifPlayerPresent(playerId -> {
					final EntityPlayer player = world.getPlayerEntityByUUID(playerId);
					final InSomniable inSomniable;
					if (player != null && (inSomniable = InSomniableCapability.getInSomniable(player)) != null) {
						inSomniable.onPlay(world, player, event.getPos(), event.getVanillaNoteId());
					}
				});
			}
		}
	}
}
