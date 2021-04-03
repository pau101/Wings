function initializeCoreMod() {
Java.type('net.minecraftforge.coremod.api.ASMAPI').loadFile('easycorelib.js')

easycore.include('me')

var PlayerEntity = net.minecraft.entity.player.PlayerEntity,
    LivingEntity = net.minecraft.entity.LivingEntity,
    Entity = net.minecraft.entity.Entity,
    WingsHooks = me.paulf.wings.server.asm.WingsHooks,
    WingsHooksClient = me.paulf.wings.server.asm.WingsHooksClient,
    ResourceLocation = net.minecraft.util.ResourceLocation,
    ServerPlayNetHandler = net.minecraft.network.play.ServerPlayNetHandler,
    ServerPlayerEntity = net.minecraft.entity.player.ServerPlayerEntity,
    CPlayerPacket = net.minecraft.network.play.client.CPlayerPacket,
    LivingEntity = net.minecraft.entity.LivingEntity,
    ActiveRenderInfo = net.minecraft.client.renderer.ActiveRenderInfo,
    FirstPersonRenderer = net.minecraft.client.renderer.FirstPersonRenderer,
    AbstractClientPlayerEntity = net.minecraft.client.entity.player.AbstractClientPlayerEntity,
    ItemStack = net.minecraft.item.ItemStack,
    PlayerModel = net.minecraft.client.renderer.entity.model.PlayerModel,
    MatrixStack = com.mojang.blaze3d.matrix.MatrixStack

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
    .atEach(invokevirtual(ServerPlayerEntity.func_184613_cA(), boolean)).append( // isElytraFlying
        aload(0),
        getfield(ServerPlayNetHandler.field_147369_b, ServerPlayerEntity), // player
        swap,
        invokestatic(WingsHooks.onFlightCheck(PlayerEntity, boolean), boolean)
    )

/**
 * Add GetCameraEyeHeightEvent
 */
easycore.inMethod(ActiveRenderInfo.func_216783_a()) // interpolateHeight
    .atFirst(invokevirtual(Entity.func_213307_e())).append( // getEyeHeight
        aload(0),
        getfield(ActiveRenderInfo.field_216791_c, Entity), // interpolateHeight
        swap,
        invokestatic(WingsHooks.onGetCameraEyeHeight(Entity, float), float)
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

/**
 * Add GetLivingHeadLimitEvent, rotate body with head rotation at limit
 */
easycore.inMethod(Entity.func_195049_a(double, double)) // rotateTowards
    .atLast(_return).prepend(
        aload(0),
        dup,
//        getfield(Entity.field_70177_z, float), // rotationYaw
//        f2d,
        dload(7),
//        dsub,
        d2f,
        invokestatic(WingsHooksClient.onTurn(Entity, float))
    )

/**
 * Make offhand always render
 */
easycore.inMethod(FirstPersonRenderer.func_228405_a_(
        AbstractClientPlayerEntity,
        float,
        float,
        net.minecraft.util.Hand,
        float,
        ItemStack,
        float,
        MatrixStack,
        net.minecraft.client.renderer.IRenderTypeBuffer,
        int
        )) // renderItemInFirstPerson
    .atFirst(iload(11)).after(ifeq).append(
        aload(0),
        getfield(FirstPersonRenderer.field_187467_d, ItemStack), // itemStackMainhand
        invokestatic(WingsHooksClient.onCheckRenderEmptyHand(boolean, ItemStack), boolean)
    )

/**
 * Replace reequip logic to control visibility of offhand, existing implementation left as dead code
 */
easycore.inMethod(net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(ItemStack, ItemStack, int), boolean)
    .atFirst().prepend(
        aload(0),
        aload(1),
        iload(2),
        invokestatic(WingsHooksClient.onCheckDoReequipAnimation(ItemStack, ItemStack, int), boolean),
        ireturn
    )

/**
 * Add AnimatePlayerModelEvent
 */
easycore.inMethod(PlayerModel.func_225597_a_(LivingEntity, float, float, float, float, float)) // setRotationAngles
    .atFirst(invokespecial(net.minecraft.client.renderer.entity.model.BipedModel.func_225597_a_(LivingEntity, float, float, float, float, float))).append(
        aload(1),
        aload(0),
        fload(4),
        fload(6),
        invokestatic(WingsHooksClient.onSetPlayerRotationAngles(LivingEntity, PlayerModel, float, float))
    )

/**
 * Add ApplyPlayerRotationsEvent
 */
easycore.inMethod(net.minecraft.client.renderer.entity.PlayerRenderer.func_225621_a_(AbstractClientPlayerEntity, MatrixStack, float, float, float)) // applyRotations
    .atLast(_return).prepend(
        aload(1),
        aload(2),
        fload(5),
        invokestatic(WingsHooksClient.onApplyPlayerRotations(AbstractClientPlayerEntity, MatrixStack, float))
    )

/**
 * Don't treat being in fall_flying pose and not elytra flying as swimming when flying
 */
easycore.inMethod(LivingEntity.func_213314_bj()) // isActualySwimming
    .atFirst(invokevirtual(LivingEntity.func_184613_cA(), boolean)).append( // isElytraFlying
        aload(0),
        swap,
        invokestatic(WingsHooks.onFlightCheck(LivingEntity, boolean), boolean)
    )

return easycore.build()
}
