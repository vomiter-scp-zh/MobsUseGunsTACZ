package com.vomiter.mobstacz.common.entity.ai;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.vomiter.mobstacz.MobsTacz;
import com.vomiter.mobstacz.common.entity.IAmmoStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.OptionalInt;

public class CrazyShooterReloadGoal extends Goal implements IShootingGoal {
    private final Mob shooter;
    private int nextAttackTick;

    public CrazyShooterReloadGoal(
            Mob shooter
    ) {
        this.shooter = shooter;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        IMobGunState mobGunState = (IMobGunState) shooter;
        return GunMode.RELOAD.equals(mobGunState.mtacz$getMode());
    }

    @Override
    public boolean canContinueToUse() {
        return nextAttackTick > 0;
    }

    @Override
    public void start() {
        //MobsTacz.LOGGER.info("[MTACZ] reload start");
        var reloadSecond = getTaczReloadTicks(shooter.getMainHandItem(), true, false);
        if(reloadSecond.isPresent()){
            nextAttackTick = reloadSecond.getAsInt() + 10;
            MobsTacz.LOGGER.info("[MTACZ] reload time of {} for entity {} is {}", shooter.getMainHandItem(), shooter, nextAttackTick);
            var gunOperator = IGunOperator.fromLivingEntity(shooter);
            gunOperator.reload();
        }
        else {
            MobsTacz.LOGGER.warn("[MTACZ] Failed to get reload time of {} for entity {}", shooter.getMainHandItem(), shooter);
            ((IAmmoStorage)shooter).mobstacz$setAmmoCount(0);
        }
    }

    @Override
    public void stop() {
        //MobsTacz.LOGGER.info("[MTACZ] reload stop");
        shooter.getNavigation().stop();
        nextAttackTick = 0;
        IMobGunState mobGunState = (IMobGunState) shooter;
        mobGunState.mtacz$setAimPitchOffset(0);
        mobGunState.mtacz$setAimYawOffset(0);
        //MobsTacz.LOGGER.info("[MTACZ] Resume shooting");
        mobGunState.mtacz$setMode(GunMode.FIRE);
        mobGunState.mtacz$getAmmo().mobstacz$reduceAmmoCount(1);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (nextAttackTick > 0) {
            nextAttackTick--;
        }
    }

    private OptionalInt getTaczReloadTicks(ItemStack stack, boolean emptyReload, boolean feedTime) {
        if (!(stack.getItem() instanceof IGun gun)) {
            return OptionalInt.empty();
        }

        ResourceLocation gunId = gun.getGunId(stack);

        return TimelessAPI.getCommonGunIndex(gunId)
                .map(CommonGunIndex::getGunData)
                .map(GunData::getReloadData)
                .map(reloadData -> feedTime ? reloadData.getFeed() : reloadData.getCooldown())
                .map(time -> emptyReload ? time.getEmptyTime() : time.getTacticalTime())
                .map(seconds -> Mth.ceil(seconds * 20.0F))
                .map(OptionalInt::of)
                .orElse(OptionalInt.empty());
    }
}