package com.vomiter.mobstacz.common.entity;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import net.minecraft.world.entity.Mob;

public final class MobGunAnimationSyncHelper {
    private MobGunAnimationSyncHelper() {}

    public static void syncFromOperator(Mob mob) {
        if (!(mob instanceof IMobGunAnimationSync sync)) {
            return;
        }

        IGunOperator operator = IGunOperator.fromLivingEntity(mob);
        ReloadState reloadState = operator.getSynReloadState();

        if (reloadState != null && reloadState.getStateType() != ReloadState.StateType.NOT_RELOADING) {
            sync.mtacz$setReloadSync(
                    true,
                    reloadState.getStateType(),
                    (int) reloadState.getCountDown()
            );
        } else {
            sync.mtacz$clearReloadSync();
        }

        sync.mtacz$setBoltingSync(operator.getSynIsBolting());
    }
}