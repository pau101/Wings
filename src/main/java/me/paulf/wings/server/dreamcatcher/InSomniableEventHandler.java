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
    private InSomniableEventHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity && !player.isCreative()) {
            World world = event.getWorld();
            BlockPos pos = event.getPos();
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (block == Blocks.NOTE_BLOCK && world.isEmptyBlock(pos.above()) &&
                world.mayInteract(player, pos) &&
                !player.blockActionRestricted(world, pos, ((ServerPlayerEntity) player).gameMode.getGameModeForPlayer())
            ) {
                InSomniableCapability.getInSomniable(player).ifPresent(inSomniable ->
                    inSomniable.onPlay(world, player, pos, state.getValue(NoteBlock.NOTE))
                );
            }
        }
    }
}
