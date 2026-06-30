package com.vomiter.mobstacz.common.entity.ai;

import com.vomiter.mobstacz.MobsTacz;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CrazyShooterAimGoal extends Goal implements IShootingGoal {
    private final Mob shooter;
    private final float minOffsetTolerance;
    private final float attackRange;

    // 射擊後準心飄移
    public CrazyShooterAimGoal(
            Mob shooter,
            float minOffsetTolerance, float attackRange
    ) {
        this.shooter = shooter;
        this.minOffsetTolerance = minOffsetTolerance;
        this.attackRange = attackRange;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        IMobGunState mobGunState = (IMobGunState) shooter;
        return GunMode.AIM.equals(mobGunState.mtacz$getMode());
    }

    @Override
    public boolean canContinueToUse() {
        IMobGunState mobGunState = (IMobGunState) shooter;
        var maxOffset
                = Math.max(
                Math.abs(mobGunState.mtacz$getAimPitchOffset()),
                Math.abs(mobGunState.mtacz$getAimYawOffset())
        );

        return maxOffset > minOffsetTolerance;
    }

    @Override
    public void start() {
        //MobsTacz.LOGGER.info("[MTACZ] aim start");
    }

    @Override
    public void stop() {
        //MobsTacz.LOGGER.info("[MTACZ] aim stop");
        IMobGunState mobGunState = (IMobGunState) shooter;
        mobGunState.mtacz$setMode(GunMode.FIRE);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = shooter.getTarget();
        if (target == null) return;

        boolean canSee = shooter.getSensing().hasLineOfSight(target);
        double targetX = target.getX();
        double targetY = target.getBoundingBox().getCenter().y;
        double targetZ = target.getZ();

        double dx = targetX - shooter.getX();
        double dy = targetY - shooter.getEyeY();
        double dz = targetZ - shooter.getZ();

        float finalYaw = (float) -Math.toDegrees(Math.atan2(dx, dz));
        float finalPitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));

        setRot(finalYaw, finalPitch);
        shooter.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distSqr = shooter.distanceToSqr(target);
        double attackRangeSqr = attackRange * attackRange;

        // 太遠就靠近，夠近就停
        if (distSqr > attackRangeSqr * 0.8D) {
            shooter.getNavigation().moveTo(target, 0);
        } else {
            shooter.getNavigation().stop();
        }

        if (!canSee) {
            return;
        }

        IMobGunState mobGunState = (IMobGunState) shooter;
        if(shooter.tickCount % 10 == 0) {
            mobGunState.mtacz$decayAimDrift();
        }

    }

    private void setRot(float yaw, float pitch) {
        shooter.setYRot(yaw);
        shooter.setXRot(pitch);
        shooter.setYHeadRot(yaw);
        shooter.yHeadRotO = yaw;
        shooter.yBodyRot = yaw;
    }

}