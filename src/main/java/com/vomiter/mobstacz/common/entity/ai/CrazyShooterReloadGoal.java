package com.vomiter.mobstacz.common.entity.ai;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.vomiter.mobstacz.common.entity.MobGunUtils;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CrazyShooterReloadGoal extends Goal implements IShootingGoal {

    private final Mob shooter;
    private boolean reloadStarted;

    public CrazyShooterReloadGoal(Mob shooter) {
        this.shooter = shooter;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        IMobGunState state = (IMobGunState) shooter;

        return state.mtacz$getMode() == GunMode.RELOAD
                && IGun.mainHandHoldGun(shooter);
    }

    @Override
    public boolean canContinueToUse() {
        IMobGunState state = (IMobGunState) shooter;

        return state.mtacz$getMode() == GunMode.RELOAD
                && IGun.mainHandHoldGun(shooter);
    }

    @Override
    public void start() {
        shooter.getNavigation().stop();

        IGunOperator operator = IGunOperator.fromLivingEntity(shooter);

        if (isReloading(operator)) {
            reloadStarted = true;
            return;
        }

        if (!MobGunUtils.canReload(shooter)) {
            reloadStarted = false;
            ((IMobGunState) shooter)
                    .mtacz$setMode(GunMode.MELEE);
            return;
        }

        operator.reload();
        reloadStarted = isReloading(operator);

        if (!reloadStarted) {
            // event 被取消、gun script 拒絕開始，或其他條件失敗。
            ((IMobGunState) shooter).mtacz$setMode(GunMode.MELEE);
        }
    }

    @Override
    public void tick() {
        if (!reloadStarted) {
            return;
        }

        IGunOperator operator = IGunOperator.fromLivingEntity(shooter);

        if (!isReloading(operator)) {
            reloadStarted = false;

            IMobGunState state = (IMobGunState) shooter;
            state.mtacz$setAimPitchOffset(0);
            state.mtacz$setAimYawOffset(0);
            state.mtacz$setMode(GunMode.RANGED);
        }
    }

    @Override
    public void stop() {
        IGunOperator operator = IGunOperator.fromLivingEntity(shooter);

        if (reloadStarted && isReloading(operator)) {
            operator.cancelReload();
        }

        reloadStarted = false;
        shooter.getNavigation().stop();
    }

    private boolean isReloading(IGunOperator operator) {
        return operator.getDataHolder()
                .reloadStateType
                .isReloading();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}