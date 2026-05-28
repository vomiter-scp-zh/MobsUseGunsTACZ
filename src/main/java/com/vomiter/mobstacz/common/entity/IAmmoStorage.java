package com.vomiter.mobstacz.common.entity;

import com.vomiter.mobstacz.MobsTacz;

public interface IAmmoStorage {
    int mobstacz$getAmmoCount();
    void mobstacz$setAmmoCount(int i);
    boolean mobstacz$hasBasicAmmo();
    void mobstacz$setHasBasicAmmo(boolean b);

    default void mobstacz$reduceAmmoCount(int i){
        MobsTacz.LOGGER.info("[MTACZ] Consuming Ammo");
        mobstacz$setAmmoCount(Math.max(mobstacz$getAmmoCount() - i, 0));
    }
}
