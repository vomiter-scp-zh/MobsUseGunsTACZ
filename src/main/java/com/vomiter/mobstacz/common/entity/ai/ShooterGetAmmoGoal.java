package com.vomiter.mobstacz.common.entity.ai;

import com.tacz.guns.api.item.IAmmo;
import com.vomiter.neurolib.common.entity.gather.MobMoveToDroppedItemGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class ShooterGetAmmoGoal extends MobMoveToDroppedItemGoal<PathfinderMob> {

    private static final int FAIL_TTL_TICKS = 600;
    private static final int STUCK_CHECK_INTERVAL_TICKS = 10;
    private static final int STUCK_MAX_TICKS = 80;
    private static final double STUCK_MIN_PROGRESS = 0.05D;
    private static final int LOSS_OF_SIGHT_MAX_TICKS = 200;
    private static final int REPATH_INTERVAL_TICKS = 10;
    private static final double EAT_HORIZONTAL_RANGE = 1.0D;
    private static final double EAT_VERTICAL_RANGE = 1.25D;
    private static final int EATING_FX_DURATION_TICKS = 40;

    public ShooterGetAmmoGoal(PathfinderMob pathfinderMob, double speed, int scanIntervalTicks, double searchRadius) {
        super(
                pathfinderMob,
                speed,
                scanIntervalTicks,
                searchRadius,
                FAIL_TTL_TICKS,
                STUCK_CHECK_INTERVAL_TICKS,
                STUCK_MAX_TICKS,
                STUCK_MIN_PROGRESS,
                LOSS_OF_SIGHT_MAX_TICKS,
                REPATH_INTERVAL_TICKS,
                EAT_HORIZONTAL_RANGE,
                EAT_VERTICAL_RANGE
        );
    }

    @Override
    protected boolean isGoalEnabled() {
        return mob.getTarget() == null;
    }

    @Override
    protected boolean canStartAction() {
        IMobGunState shooterState = (IMobGunState) mob;
        if(shooterState.getGunIndex().isEmpty()) return false;
        return !shooterState.canReload();
    }

    @Override
    protected boolean canContinueAction() {
        IMobGunState shooterState = (IMobGunState) mob;
        if(shooterState.getGunIndex().isEmpty()) return false;
        return !shooterState.canReload();
    }

    @Override
    protected boolean isValidTarget(ItemStack stack, ItemEntity entity) {
        IMobGunState shooterState = (IMobGunState) mob;
        var gunIndexOpt = shooterState.getGunIndex();
        if (gunIndexOpt.isPresent()){
            var ammo = IAmmo.getIAmmoOrNull(stack);
            if(ammo != null){
                return ammo.isAmmoOfGun(mob.getMainHandItem(), stack);
            }
        }
        return false;
    }

    @Override
    protected void onReachedTarget(ItemEntity target) {
        IMobGunState shooterState = (IMobGunState) mob;
        shooterState.mtacz$getAmmo().mobstacz$addAmmoCount(target.getItem().getCount());
        target.discard();
        shooterState.mtacz$setMode(GunMode.RELOAD);
    }


    @Override
    protected int getActionCooldownTicks() {
        return 20;
    }

    @Override
    protected void onStart() {
        mob.setTarget(null);
    }

    @Override
    protected boolean isCloseEnoughToInteract(ItemEntity item) {
        if(mob.getVehicle() == null) return super.isCloseEnoughToInteract(item);
        double dx = mob.getX() - item.getX();
        double dz = mob.getZ() - item.getZ();
        double dy = Math.abs(mob.getVehicle().getY() - item.getY());

        return dx * dx + dz * dz <= interactHorizontalRange * interactHorizontalRange
                && dy <= interactVerticalRange;
    }

}