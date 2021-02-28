package me.paulf.wings.server.dreamcatcher;

import me.paulf.wings.WingsMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class InSomniableEventHandler {
	private InSomniableEventHandler() {}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onLeftClickBlock(final PlayerInteractEvent.LeftClickBlock event) {
		final PlayerEntity player = event.getPlayer();
		if (player instanceof ServerPlayerEntity && !player.isCreative()) {
			final World world = event.getWorld();
			final BlockPos pos = event.getPos();
			final BlockState state = world.getBlockState(pos);
			final Block block = state.getBlock();
			if (block == Blocks.NOTE_BLOCK && world.isAirBlock(pos.up()) &&
				world.isBlockModifiable(player, pos) &&
				!player.blockActionRestricted(world, pos, ((ServerPlayerEntity) player).interactionManager.getGameType())
			) {
				InSomniableCapability.getInSomniable(player).ifPresent(inSomniable ->
					inSomniable.onPlay(world, player, pos, state.get(NoteBlock.NOTE))
				);
			}
		}
	}
}
