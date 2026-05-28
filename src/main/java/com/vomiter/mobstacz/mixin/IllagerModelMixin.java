package com.vomiter.mobstacz.mixin;

import com.vomiter.mobstacz.client.animation.IGunPoseModelAccess;
import com.vomiter.mobstacz.client.animation.MobGunAnimationApplier;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.AbstractIllager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IllagerModel.class)
public class IllagerModelMixin<T extends AbstractIllager> {
    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V",
            at = @At("TAIL")
    )
    private void mtacz$reapplyGunPose(
            Entity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo ci
    ) {
        MobGunAnimationApplier.applyIfNeeded((IGunPoseModelAccess) this, (T)entity, ageInTicks);
    }

}
