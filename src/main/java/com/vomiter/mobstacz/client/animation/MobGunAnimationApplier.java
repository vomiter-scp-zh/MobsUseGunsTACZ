package com.vomiter.mobstacz.client.animation;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.api.item.IGun;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public final class MobGunAnimationApplier {

    public static <T extends LivingEntity> void applyIfNeeded(
            IGunPoseModelAccess model,
            T entity,
            float ageInTicks
    ) {
        if (!(entity instanceof Mob mob)) {
            return;
        }

        if (IGun.getIGunOrNull(mob.getMainHandItem()) == null) {
            return;
        }

        IGunOperator operator = IGunOperator.fromLivingEntity(mob);
        ReloadState reloadState = operator.getSynReloadState();

        if (reloadState.getStateType().isReloading()) {
            float progress = TaczClientReloadHelper.getReloadPhaseProgress(
                    mob.getMainHandItem(),
                    reloadState
            );
            MobGunPoseHelper.applyReloadPose(
                    model,
                    reloadState.getStateType(),
                    progress
            );
        } else if (operator.getSynIsBolting()) {
            // bolt pose
        } else {
            MobGunPoseHelper.applyAimingPose(model);
        }
    }
}