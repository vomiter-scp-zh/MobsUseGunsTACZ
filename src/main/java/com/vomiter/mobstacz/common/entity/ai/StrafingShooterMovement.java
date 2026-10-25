package com.vomiter.mobstacz.common.entity.ai;

import com.vomiter.mobstacz.data.mtacz.MobTaczMovementConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;

import java.util.Objects;
import java.util.function.Supplier;

/** Share one instance between a single mob's aim and shooting goals. */
public final class StrafingShooterMovement implements ShooterMovement {
    private final Supplier<MobTaczMovementConfig> config;
    private LivingEntity lastTarget;
    private Vec3 origin;
    private Vec3 axis;
    private long lastTick = Long.MIN_VALUE;
    private boolean positive;

    public StrafingShooterMovement(Supplier<MobTaczMovementConfig> config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public void tick(Mob shooter, LivingEntity target, boolean canSee, double moveSpeed, float attackRange) {
        MobTaczMovementConfig settings = config.get();
        long now = shooter.level().getGameTime();
        boolean strafe = canSee && shooter.onGround()
                && settings.strafeAmplitude() > 0 && settings.strafeSpeed() > 0
                && shooter.distanceToSqr(target) <= attackRange * attackRange * 0.8D;
        if (!strafe) {
            // Clear STRAFE only on transition, preserving ongoing navigation paths.
            if (origin != null) stop(shooter);
            reset();
            ShooterMovement.APPROACH.tick(shooter, target, canSee, moveSpeed, attackRange);
            if (!canSee && shooter.distanceToSqr(target) <= attackRange * attackRange * 0.8D) {
                shooter.getNavigation().moveTo(target, moveSpeed / 2);
            }
            return;
        }

        if (origin == null || lastTarget != target || now - lastTick > 2
                || shooter.position().distanceToSqr(origin) > 4096) {
            origin = shooter.position();
            Vec3 toward = target.position().subtract(origin).multiply(1, 0, 1).normalize();
            if (toward.lengthSqr() < 1.0E-6) {
                reset();
                stop(shooter);
                return;
            }
            // Fixed world-space lateral axis; target rotation cannot move the corridor.
            axis = new Vec3(toward.z, 0, -toward.x);
            lastTarget = target;
            positive = shooter.getRandom().nextBoolean();
        }
        lastTick = now;
        double offset = shooter.position().subtract(origin).dot(axis);
        if (offset >= settings.strafeAmplitude()) positive = false;
        else if (offset <= -settings.strafeAmplitude()) positive = true;
        else if (shooter.horizontalCollision) positive = !positive;

        Vec3 direction = axis.scale(positive ? 1 : -1);
        if (!canStep(shooter, direction)) {
            positive = !positive;
            direction = direction.scale(-1);
            if (!canStep(shooter, direction)) {
                stop(shooter);
                return;
            }
        }
        double yaw = Math.toRadians(shooter.getYRot());
        // Convert fixed world direction to local input, keeping the gun facing its target.
        float forward = (float) (-direction.x * Math.sin(yaw) + direction.z * Math.cos(yaw));
        float sideways = (float) (direction.x * Math.cos(yaw) + direction.z * Math.sin(yaw));
        shooter.getNavigation().stop();
        shooter.getMoveControl().strafe(forward * settings.strafeSpeed(), sideways * settings.strafeSpeed());
    }

    private boolean canStep(Mob shooter, Vec3 direction) {
        Vec3 step = direction.scale(Math.max(0.5D, shooter.getBbWidth()));
        BlockPos pos = BlockPos.containing(shooter.position().add(step));
        NodeEvaluator evaluator = shooter.getNavigation().getNodeEvaluator();
        return evaluator != null
                && evaluator.getBlockPathType(shooter.level(), pos.getX(), pos.getY(), pos.getZ())
                == BlockPathTypes.WALKABLE
                && shooter.level().noCollision(shooter, shooter.getBoundingBox().move(step));
    }

    private void reset() {
        origin = null;
        axis = null;
        lastTarget = null;
    }
}
