package com.vomiter.mobstacz.client.animation;

import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.api.item.IGun;
import com.vomiter.mobstacz.common.entity.IMobGunAnimationSync;
import com.vomiter.mobstacz.common.gun.TaczReloadHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class MobGunAnimationApplier {
    private MobGunAnimationApplier() {
    }

    public static <T extends LivingEntity> void applyIfNeeded(
            IGunPoseModelAccess model,
            T entity,
            float ageInTicks
    ) {
        if (!(entity instanceof IMobGunAnimationSync sync)) {
            return;
        }
        if(IGun.getIGunOrNull(entity.getMainHandItem()) == null) return;

        if (sync.mtacz$isReloadingSynced()) {
            ReloadState.StateType type = sync.mtacz$getReloadStateTypeSynced();
            int countDown = sync.mtacz$getReloadCountdownSynced();

            ReloadState state = new ReloadState();
            state.setStateType(type);
            state.setCountDown(countDown);

            ItemStack gunStack = entity.getMainHandItem();
            float progress = TaczReloadHelper.getReloadPhaseProgress(gunStack, state);

            MobGunPoseHelper.applyReloadPose(model, type, progress);
        }

        else if (sync.mtacz$isBoltingSynced()) {
            float progress = MobGunPoseHelper.getBoltProgress(ageInTicks);
            MobGunPoseHelper.applyBoltPose(model, progress);
        }

        else {
            MobGunPoseHelper.applyAimingPose(model);
        }


    }
}