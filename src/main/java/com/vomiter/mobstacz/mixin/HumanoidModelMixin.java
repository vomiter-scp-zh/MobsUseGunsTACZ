package com.vomiter.mobstacz.mixin;

import com.vomiter.mobstacz.client.animation.IGunPoseModelAccess;
import com.vomiter.mobstacz.client.animation.MobGunAnimationApplier;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> extends AgeableListModel<T> {
    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
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
        if ((Object) this instanceof AbstractZombieModel<?>) {
            return;
        }
        if(entity instanceof Zombie) return;

        MobGunAnimationApplier.applyIfNeeded((IGunPoseModelAccess) this, entity, ageInTicks);
    }
}