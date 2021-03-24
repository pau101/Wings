function initializeCoreMod() {
Java.type('net.minecraftforge.coremod.api.ASMAPI').loadFile('easycorelib.js')

easycore.include('me')

var PlayerEntity = net.minecraft.entity.player.PlayerEntity,
    LivingEntity = net.minecraft.entity.LivingEntity,
    WingsHooks = me.paulf.wings.server.asm.WingsHooks,
    ResourceLocation = net.minecraft.util.ResourceLocation,
    ServerPlayNetHandler = net.minecraft.network.play.ServerPlayNetHandler,
    ServerPlayerEntity = net.minecraft.entity.player.ServerPlayerEntity,
    CPlayerPacket = net.minecraft.network.play.client.CPlayerPacket,
    LivingEntity = net.minecraft.entity.LivingEntity

/**
 * Enable flying pose for wings
 */
easycore.inMethod(PlayerEntity.func_213832_dB()) // updatePose
    .atFirst(invokevirtual(PlayerEntity.func_184613_cA(), boolean)).append( // isElytraFlying
        aload(0),
        swap,
        invokestatic(WingsHooks.onFlightCheck(PlayerEntity, boolean), boolean)
    )

/**
 * Add exhaustion for winged flight
 */
easycore.inMethod(PlayerEntity.func_71000_j(double, double, double)) // addMovementStat
    .atLast(invokestatic(java.lang.Math.round(float), int)).append(
        aload(0),
        dload(1),
        dload(3),
        dload(5),
        invokestatic(WingsHooks.onAddFlown(PlayerEntity, double, double, double))
    )

/**
 * Use flying speed for player movement validation
 */
easycore.inMethod(ServerPlayNetHandler.func_147347_a(CPlayerPacket)) // processPlayer
    .atEach(invokevirtual(ServerPlayNetHandler.func_184613_cA(), boolean)).append( // isElytraFlying
        aload(0),
        getfield(ServerPlayNetHandler.field_147369_b, ServerPlayerEntity), // player
        swap,
        invokestatic(WingsHooks.onFlightCheck(PlayerEntity, boolean), boolean)
    )

/**
 * Add smooth body rotation while flying
 */
easycore.inMethod(LivingEntity.func_110146_f(float, float), float) // updateDistance
    .atFirst().prepend(
        aload(0),
        fload(1),
        invokestatic(WingsHooks.onUpdateBodyRotation(LivingEntity, float), boolean),
        ifeq(L0 = label()),
        bipush(0),
        i2f,
        freturn,
        L0
    )

return easycore.build()
}