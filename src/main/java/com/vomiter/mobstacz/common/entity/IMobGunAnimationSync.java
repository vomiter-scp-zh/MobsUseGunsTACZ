package com.vomiter.mobstacz.common.entity;

import com.tacz.guns.api.entity.ReloadState;

public interface IMobGunAnimationSync {
    void mtacz$setReloadSync(boolean reloading, ReloadState.StateType type, int countdown);
    void mtacz$clearReloadSync();
    void mtacz$setBoltingSync(boolean bolting);

    boolean mtacz$isReloadingSynced();
    ReloadState.StateType mtacz$getReloadStateTypeSynced();
    int mtacz$getReloadCountdownSynced();
    boolean mtacz$isBoltingSynced();
}