package com.vomiter.mobstacz.mixin;

import com.vomiter.mobstacz.client.animation.IGunPoseModelAccess;
import com.vomiter.mobstacz.client.animation.MobGunAnimationApplier;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractZombieModel.class)
public abstract class AbstractZombieModelMixin<T extends Monster> extends HumanoidModel<T> {
    protected AbstractZombieModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/monster/Monster;FFFFF)V",
            at = @At("TAIL")
    )
    private void mtacz$reapplyGunPose(
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo ci
    ) {
        MobGunAnimationApplier.applyIfNeeded((IGunPoseModelAccess) this, entity, ageInTicks);
    }
}