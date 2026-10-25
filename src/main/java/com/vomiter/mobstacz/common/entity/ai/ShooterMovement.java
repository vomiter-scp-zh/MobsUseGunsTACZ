package com.vomiter.mobstacz.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/** Per-mob movement policy. Goals retain ownership of MOVE and LOOK flags. */
@FunctionalInterface
public interface ShooterMovement {
    void tick(Mob shooter, LivingEntity target, boolean canSee, double moveSpeed, float attackRange);

    /** Release movement inputs without discarding state shared by aim/fire goals. */
    default void stop(Mob shooter) {
        shooter.getNavigation().stop();
        shooter.getMoveControl().setWantedPosition(shooter.getX(), shooter.getY(), shooter.getZ(), 0);
        shooter.setXxa(0);
        shooter.setZza(0);
    }

    /** Original approach-and-stop behavior, for callers using the old constructors. */
    ShooterMovement APPROACH = (shooter, target, canSee, moveSpeed, attackRange) -> {
        double distance = shooter.distanceToSqr(target);
        double range = attackRange * attackRange;
        if (distance > range * 2.25D) {
            shooter.getNavigation().moveTo(target, moveSpeed);
        } else if (distance > range * 0.8D) {
            shooter.getNavigation().moveTo(target, moveSpeed / 2);
        } else {
            shooter.getNavigation().stop();
        }
    };
}
