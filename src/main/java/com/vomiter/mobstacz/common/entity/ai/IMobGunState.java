package com.vomiter.mobstacz.common.entity.ai;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.vomiter.mobstacz.common.entity.IAmmoStorage;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

import java.util.Objects;
import java.util.Optional;

public interface IMobGunState {
    GunMode mtacz$getMode();
    void mtacz$setMode(GunMode mode);
    float mtacz$getAimYawOffset();
    void mtacz$setAimYawOffset(float aimYawOffset);
    float mtacz$getAimPitchOffset();
    void mtacz$setAimPitchOffset(float aimPitchOffset);
    default void mtacz$addAimPitchOffset(float delta){
        mtacz$setAimPitchOffset(mtacz$getAimPitchOffset() + delta);
    }
    default void mtacz$addAimYawOffset(float delta){
        mtacz$setAimYawOffset(mtacz$getAimYawOffset() + delta);
    }
    default void mtacz$clampPitchOffset(float a, float b){
        mtacz$setAimPitchOffset(Mth.clamp(a, mtacz$getAimPitchOffset(), b));
    }
    default void mtacz$clampYawOffset(float a, float b){
        mtacz$setAimYawOffset(Mth.clamp(a, mtacz$getAimYawOffset(), b));
    }
    default void mtacz$decayAimDrift() {
        mtacz$setAimPitchOffset(approachZero(mtacz$getAimPitchOffset(), 0.7F));
        mtacz$setAimYawOffset(approachZero(mtacz$getAimYawOffset(), 0.5F));
    }

    default IAmmoStorage mtacz$getAmmo(){
        if(this instanceof IAmmoStorage ammoStorage) return ammoStorage;
        return null;
    }

    /**
     * false if no ammo;
     */
    default boolean canReload(){
        return mtacz$getAmmo().mobstacz$getAmmoCount() > 0;
    }

    default Optional<CommonGunIndex> getGunIndex(){
        if(this instanceof Mob mob && IGun.mainHandHoldGun(mob)){
            var gunId = Objects.requireNonNull(IGun.getIGunOrNull(mob.getMainHandItem()))
                    .getGunId(mob.getMainHandItem());
            return TimelessAPI.getCommonGunIndex(gunId);
        }
        return Optional.empty();
    }

    private float approachZero(float value, float amount) {
        if (value > 0) return Math.max(0, value - amount);
        if (value < 0) return Math.min(0, value + amount);
        return 0;
    }


}
