package com.vomiter.mobstacz.common.entity.ai;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.custom.RecoilModifier;
import com.tacz.guns.resource.pojo.data.gun.GunRecoil;
import com.tacz.guns.resource.pojo.data.gun.GunRecoilKeyFrame;
import net.minecraft.world.item.ItemStack;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.api.item.IGun;
import com.vomiter.mobstacz.Config;
import com.vomiter.mobstacz.common.entity.MobGunUtils;
import com.vomiter.mobstacz.common.registry.ModEntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Objects;

public class CrazyShooterShootingGoal extends Goal implements IShootingGoal {
    private final Mob shooter;
    private final double moveSpeed;
    private final ShooterMovement movement;
    private final float attackRange;
    private final int minAttackInterval;
    private final int maxAttackInterval;
    private final int loseSightTolerance;
    private final float offsetTolerance;


    private int nextAttackTick;
    private int additionalShootCooldownTicks;
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
        this(shooter, moveSpeed, attackRange, minAttackInterval, maxAttackInterval,
                loseSightTolerance, offsetTolerance, ShooterMovement.APPROACH);
    }

    public CrazyShooterShootingGoal(Mob shooter, double moveSpeed, float attackRange,
                                    int minAttackInterval, int maxAttackInterval,
                                    int loseSightTolerance, float offsetTolerance,
                                    ShooterMovement movement) {
        this.movement = Objects.requireNonNull(movement);
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
        additionalShootCooldownTicks = 0;
        unseenTicks = 0;
    }

    @Override
    public void stop() {
        //MobsTacz.LOGGER.info("[MTACZ] fire stop");
        movement.stop(shooter);
        nextAttackTick = 0;
        additionalShootCooldownTicks = 0;
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

        movement.tick(shooter, target, canSee, moveSpeed, attackRange);

        if (!canSee) {
            return;
        }

        if (nextAttackTick > 0) {
            nextAttackTick--;
            return;
        }

        IMobGunState shooterState = (IMobGunState) shooter;
        if (additionalShootCooldownTicks > 0) {
            if (shouldApplyAdditionalCooldown(shooterState)) {
                additionalShootCooldownTicks--;
                return;
            }
            additionalShootCooldownTicks = 0;
        }

        tryOperateGun(finalPitch, finalYaw, shooterState);
    }

    private boolean shouldApplyAdditionalCooldown(IMobGunState state) {
        Boolean override = state.mtacz$getAdditionalShootCooldownOverride();
        if (override != null) {
            return override;
        }

        EntityType<?> type = shooter.getType();
        if (type.is(ModEntityTypeTags.ADDITIONAL_SHOOT_COOLDOWN_BLACKLIST)) {
            return false;
        }
        return type.is(ModEntityTypeTags.ADDITIONAL_SHOOT_COOLDOWN_WHITELIST)
                || Config.ADDITIONAL_SHOOT_COOLDOWN;
    }

    private void tryOperateGun(float finalPitch, float finalYaw, IMobGunState shooterState) {
        IGunOperator gunOperator = IGunOperator.fromLivingEntity(shooter);
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
        ItemStack gunStack = shooter.getMainHandItem();
        IGun gun = IGun.getIGunOrNull(gunStack);
        if (gun == null) {
            return;
        }

        GunRecoil recoil = TimelessAPI.getCommonGunIndex(gun.getGunId(gunStack))
                .map(index -> index.getGunData().getRecoil())
                .orElse(null);
        if (recoil == null) {
            return;
        }

        float pitchModifier = 1.0F;
        float yawModifier = 1.0F;
        AttachmentCacheProperty cache = IGunOperator.fromLivingEntity(shooter).getCacheProperty();
        if (cache != null) {
            ParameterizedCachePair<Float, Float> modifiers = cache.getCache(RecoilModifier.ID);
            pitchModifier = (float) modifiers.left().eval(1.0F);
            yawModifier = (float) modifiers.right().eval(1.0F);
        }

        // Use the first keyframe as a single kick; the aiming goal handles recovery.
        // TACZ subtracts recoil angles from entity rotation, so preserve that sign here.
        float pitchKick = sampleRecoilKick(recoil.getPitch()) * pitchModifier;
        float yawKick = sampleRecoilKick(recoil.getYaw()) * yawModifier;
        IMobGunState shooterState = (IMobGunState) shooter;
        shooterState.mtacz$addAimPitchOffset(-pitchKick);
        shooterState.mtacz$addAimYawOffset(-yawKick);
        shooterState.mtacz$clampYawOffset(-10f, 10f);
        shooterState.mtacz$clampPitchOffset(-12, 6);
    }

    private float sampleRecoilKick(GunRecoilKeyFrame[] frames) {
        if (frames == null || frames.length == 0) {
            return 0.0F;
        }
        float[] range = frames[0].getValue();
        return range[0] + shooter.getRandom().nextFloat() * (range[1] - range[0]);
    }

    private int randomBetween(int min, int max) {
        if (max <= min) return min;
        return min + shooter.getRandom().nextInt(max - min + 1);
    }

    private void handleShootResult(ShootResult result, IGunOperator operator, IMobGunState state) {
        switch (result) {
            case SUCCESS -> {
                applyRecoilDrift();
                if (shouldApplyAdditionalCooldown(state)) {
                    additionalShootCooldownTicks = randomBetween(minAttackInterval, maxAttackInterval);
                }
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

            case COOL_DOWN -> nextAttackTick = 0;

            case IS_DRAWING, IS_BOLTING -> {
                nextAttackTick = 4;
            }

            case OVERHEATED -> {
                nextAttackTick = 10;
            }

            default -> nextAttackTick = 10;
        }
    }
}