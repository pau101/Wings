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
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player instanceof EntityPlayerMP) {
			World world = event.getWorld();
			BlockPos pos = event.getPos();
			Block block = world.getBlockState(pos).getBlock();
			if (block == Blocks.NOTEBLOCK && canEdit((EntityPlayerMP) player, block)) {
				TileEntity entity = world.getTileEntity(pos);
				if (entity instanceof TileEntityNote) {
					InSomniableCapability.get((TileEntityNote) entity).setPlayer(player.getUniqueID());
				}
			}
		}
	}

	private static boolean canEdit(EntityPlayerMP player, Block block) {
		GameType gameType = player.interactionManager.getGameType();
		if (!gameType.hasLimitedInteractions()) {
			return true;
		}
		if (gameType == GameType.SPECTATOR) {
			return false;
		}
		if (player.isAllowEdit()) {
			return true;
		}
		ItemStack stack = player.getHeldItemMainhand();
		return !stack.isEmpty() && stack.canDestroy(block);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onNoteBlockPlay(NoteBlockEvent.Play event) {
		World world = event.getWorld();
		if (!world.isRemote) {
			TileEntity entity = world.getTileEntity(event.getPos());
			if (entity instanceof TileEntityNote) {
				InSomniableCapability.get((TileEntityNote) entity).ifPlayerPresent(playerId -> {
					EntityPlayer player = world.getPlayerEntityByUUID(playerId);
					if (player != null) {
						InSomniableCapability.get(player).onPlay(world, player, event.getPos(), event.getVanillaNoteId());
					}
				});
			}
		}
	}
}
