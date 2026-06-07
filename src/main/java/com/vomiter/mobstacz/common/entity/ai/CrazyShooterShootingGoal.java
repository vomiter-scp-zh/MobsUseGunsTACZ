package com.vomiter.mobstacz.common.entity.ai;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.api.item.IGun;
import com.vomiter.mobstacz.Config;
import com.vomiter.mobstacz.MobsTacz;
import com.vomiter.mobstacz.common.entity.IAmmoStorage;
import com.vomiter.mobstacz.common.entity.MobGunAnimationSyncHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class CrazyShooterShootingGoal extends Goal implements IShootingGoal {
    private final Mob shooter;
    private final double moveSpeed;
    private final float attackRange;
    private final int minAttackInterval;
    private final int maxAttackInterval;
    private final int loseSightTolerance;

    private int nextAttackTick;
    private int unseenTicks;

    // 射擊後準心飄移
    public CrazyShooterShootingGoal(
            Mob shooter,
            double moveSpeed,
            float attackRange,
            int minAttackInterval,
            int maxAttackInterval,
            int loseSightTolerance
    ) {
        this.shooter = shooter;
        this.moveSpeed = moveSpeed;
        this.attackRange = attackRange;
        this.minAttackInterval = minAttackInterval;
        this.maxAttackInterval = maxAttackInterval;
        this.loseSightTolerance = loseSightTolerance;

        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = shooter.getTarget();
        return target != null
                && target.isAlive()
                && IGun.mainHandHoldGun(shooter)
                && shooter.distanceToSqr(target) <= (attackRange * attackRange) * 2.25
                ;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = shooter.getTarget();
        return target != null
                && target.isAlive()
                && IGun.mainHandHoldGun(shooter)
                && unseenTicks <= loseSightTolerance
                && shooter.distanceToSqr(target) <= (attackRange * attackRange) * 2.25
                ;
    }

    @Override
    public void start() {
        nextAttackTick = 0;
        unseenTicks = 0;
    }

    @Override
    public void stop() {
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
        IGunState shooterState = (IGunState)(shooter);
        if (target == null) return;

        boolean canSee = shooter.getSensing().hasLineOfSight(target);
        MobGunAnimationSyncHelper.syncFromOperator(shooter);

        if (canSee) {
            unseenTicks = 0;
        } else {
            unseenTicks++;
        }

        if(shooter.tickCount % 10 == 0) shooterState.mtacz$decayAimDrift();

        double dx = target.getX() - shooter.getX();
        double dy = target.getEyeY() - shooter.getEyeY();
        double dz = target.getZ() - shooter.getZ();

        float baseYaw = (float) -Math.toDegrees(Math.atan2(dx, dz));
        float basePitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));

        float finalYaw = baseYaw + shooterState.mtacz$getAimYawOffset();
        float finalPitch = basePitch + shooterState.mtacz$getAimPitchOffset();

        setRot(finalYaw, finalPitch);
        shooter.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distSqr = shooter.distanceToSqr(target);
        double attackRangeSqr = attackRange * attackRange;

        // 很簡單的距離控制：太遠就靠近，夠近就停
        if (distSqr > attackRangeSqr * 0.8D) {
            shooter.getNavigation().moveTo(target, moveSpeed);
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
        ShootResult result = gunOperator.shoot(() -> finalPitch, () -> finalYaw);

        switch (result) {
            case SUCCESS -> {
                applyRecoilDrift();
                if(Config.MOB_GLOWS_AFTER_SHOOTING) shooter.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600, 0));
                nextAttackTick = randomBetween(minAttackInterval, maxAttackInterval);
            }
            case NOT_DRAW -> {
                gunOperator.draw(shooter::getMainHandItem);
                nextAttackTick = 4;
            }
            case NO_AMMO -> {
                MobsTacz.LOGGER.info("[MTACZ] Mob is reloading");
                if(shooter instanceof IAmmoStorage storage){
                    MobsTacz.LOGGER.info("[MTACZ] Ammo Storage={}", storage.mobstacz$getAmmoCount());
                    if(storage.mobstacz$getAmmoCount() <= 0){
                        shooter.spawnAtLocation(shooter.getItemBySlot(EquipmentSlot.MAINHAND));
                        shooter.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    }
                    storage.mobstacz$reduceAmmoCount(1);
                }
                gunOperator.reload();
                MobGunAnimationSyncHelper.syncFromOperator(shooter);
                nextAttackTick = 100;
            }
            case NEED_BOLT -> {
                nextAttackTick = 6;
            }
            case COOL_DOWN, IS_RELOADING, IS_DRAWING, IS_BOLTING, OVERHEATED -> {
                nextAttackTick = 4;
            }
            case IS_SPRINTING -> {
                // Mob 通常不太會 sprint 開槍，但還是保守處理
                nextAttackTick = 4;
            }
            case IS_MELEE -> {
                // 代表武器/狀態目前切到近戰流程，先稍等
                nextAttackTick = 6;
            }
            case NOT_GUN, ID_NOT_EXIST, NETWORK_FAIL, FORGE_EVENT_CANCEL, UNKNOWN_FAIL -> {
                nextAttackTick = 10;
            }
        }
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
        IGunState shooterState = (IGunState)(shooter);
        shooterState.mtacz$addAimYawOffset((shooter.getRandom().nextFloat() - 0.5F) * 2.4F);
        shooterState.mtacz$addAimPitchOffset(-2.0F - shooter.getRandom().nextFloat() * 1.5F);
        shooterState.mtacz$clampYawOffset(-10f, 10f);
        shooterState.mtacz$clampPitchOffset(-12, 6);
    }

    private int randomBetween(int min, int max) {
        if (max <= min) return min;
        return min + shooter.getRandom().nextInt(max - min + 1);
    }
}