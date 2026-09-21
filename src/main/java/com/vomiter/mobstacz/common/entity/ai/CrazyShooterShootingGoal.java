package com.vomiter.mobstacz.common.entity.ai;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.api.item.IGun;
import com.vomiter.mobstacz.Config;
import com.vomiter.mobstacz.common.entity.MobGunUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CrazyShooterShootingGoal extends Goal implements IShootingGoal {
    private final Mob shooter;
    private final double moveSpeed;
    private final float attackRange;
    private final int minAttackInterval;
    private final int maxAttackInterval;
    private final int loseSightTolerance;
    private final float offsetTolerance;


    private int nextAttackTick;
    private int unseenTicks;

    // 射擊後準心飄移
    public CrazyShooterShootingGoal(
            Mob shooter,
            double moveSpeed,
            float attackRange,
            int minAttackInterval,
            int maxAttackInterval,
            int loseSightTolerance,
            float offsetTolerance
    ) {
        this.shooter = shooter;
        this.moveSpeed = moveSpeed;
        this.attackRange = attackRange;
        this.minAttackInterval = minAttackInterval;
        this.maxAttackInterval = maxAttackInterval;
        this.loseSightTolerance = loseSightTolerance;
        this.offsetTolerance = offsetTolerance;

        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return canFireGoalRun();
    }

    @Override
    public boolean canContinueToUse() {
        return canFireGoalRun()
                && unseenTicks <= loseSightTolerance;
    }

    private boolean canFireGoalRun() {
        LivingEntity target = shooter.getTarget();
        IMobGunState state = (IMobGunState) shooter;

        return state.mtacz$getMode() == GunMode.RANGED
                && state.mtacz$getMaxAimOffset() <= offsetTolerance
                && target != null
                && target.isAlive()
                && IGun.mainHandHoldGun(shooter);
    }

    @Override
    public void start() {
        //MobsTacz.LOGGER.info("[MTACZ] fire start");
        nextAttackTick = 0;
        unseenTicks = 0;
    }

    @Override
    public void stop() {
        //MobsTacz.LOGGER.info("[MTACZ] fire stop");
        shooter.getNavigation().stop();
        nextAttackTick = 0;
        unseenTicks = 0;
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

        if (canSee) {
            unseenTicks = 0;
        } else {
            unseenTicks++;
        }

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
        if (distSqr > attackRangeSqr * 2.25D){
            shooter.getNavigation().moveTo(target, moveSpeed);
        }
        else if (distSqr > attackRangeSqr * 0.8D) {
            shooter.getNavigation().moveTo(target, moveSpeed / 2);
        } else {
            shooter.getNavigation().stop();
        }

        if (!canSee) {
            return;
        }

        if (nextAttackTick > 0) {
            nextAttackTick--;
            return;
        }

        tryOperateGun(finalPitch, finalYaw);
    }

    private void tryOperateGun(float finalPitch, float finalYaw) {
        IGunOperator gunOperator = IGunOperator.fromLivingEntity(shooter);
        IMobGunState shooterState = (IMobGunState) shooter;
        ShootResult result = gunOperator.shoot(() -> finalPitch + shooterState.mtacz$getAimPitchOffset(), () -> finalYaw + shooterState.mtacz$getAimYawOffset());
        handleShootResult(result, gunOperator, shooterState);
    }

    private void setRot(float yaw, float pitch) {
        shooter.setYRot(yaw);
        shooter.setXRot(pitch);
        shooter.setYHeadRot(yaw);
        shooter.yHeadRotO = yaw;
        shooter.yBodyRot = yaw;
    }

    private void applyRecoilDrift() {
        // 向上飄 + 左右隨機
        IMobGunState shooterState = (IMobGunState)(shooter);
        shooterState.mtacz$addAimYawOffset((shooter.getRandom().nextFloat() - 0.5F) * 2.4F);
        shooterState.mtacz$addAimPitchOffset(-2.0F - shooter.getRandom().nextFloat() * 1.5F);
        shooterState.mtacz$clampYawOffset(-10f, 10f);
        shooterState.mtacz$clampPitchOffset(-12, 6);
    }

    private int randomBetween(int min, int max) {
        if (max <= min) return min;
        return min + shooter.getRandom().nextInt(max - min + 1);
    }

    private void handleShootResult(ShootResult result, IGunOperator operator, IMobGunState state) {
        switch (result) {
            case SUCCESS -> {
                applyRecoilDrift();
                nextAttackTick = randomBetween(
                        minAttackInterval,
                        maxAttackInterval
                );
                if(Config.MOB_GLOWS_AFTER_SHOOTING){
                    shooter.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
                }
            }

            case NOT_DRAW -> {
                operator.draw(shooter::getMainHandItem);
                nextAttackTick = 4;
            }

            case NEED_BOLT -> {
                operator.bolt();
                nextAttackTick = 4;
            }

            case NO_AMMO -> {
                if (MobGunUtils.canReload(shooter)) {
                    state.mtacz$setMode(GunMode.RELOAD);
                } else {
                    state.mtacz$setMode(GunMode.MELEE);
                }
            }

            case IS_RELOADING -> {
                // 例如其他系統已經讓 TACZ 開始換彈。
                state.mtacz$setMode(GunMode.RELOAD);
            }

            case COOL_DOWN, IS_DRAWING, IS_BOLTING -> {
                nextAttackTick = 4;
            }

            case OVERHEATED -> {
                nextAttackTick = 10;
            }

            default -> nextAttackTick = 10;
        }
    }
}