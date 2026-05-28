package com.vomiter.mobstacz.mixin;

import com.tacz.guns.api.entity.ReloadState;
import com.vomiter.mobstacz.common.entity.IMobGunAnimationSync;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobGunAnimationSyncMixin implements IMobGunAnimationSync {
    @Unique
    private static final EntityDataAccessor<Boolean> MTACZ_RELOADING =
            SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BOOLEAN);

    @Unique
    private static final EntityDataAccessor<Integer> MTACZ_RELOAD_STATE_ORDINAL =
            SynchedEntityData.defineId(Mob.class, EntityDataSerializers.INT);

    @Unique
    private static final EntityDataAccessor<Integer> MTACZ_RELOAD_COUNTDOWN =
            SynchedEntityData.defineId(Mob.class, EntityDataSerializers.INT);

    @Unique
    private static final EntityDataAccessor<Boolean> MTACZ_BOLTING =
            SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void mtacz$defineGunAnimationSyncData(CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        self.getEntityData().define(MTACZ_RELOADING, false);
        self.getEntityData().define(MTACZ_RELOAD_STATE_ORDINAL, ReloadState.StateType.NOT_RELOADING.ordinal());
        self.getEntityData().define(MTACZ_RELOAD_COUNTDOWN, 0);
        self.getEntityData().define(MTACZ_BOLTING, false);
    }

    @Override
    public void mtacz$setReloadSync(boolean reloading, ReloadState.StateType type, int countdown) {
        Mob self = (Mob) (Object) this;
        self.getEntityData().set(MTACZ_RELOADING, reloading);
        self.getEntityData().set(MTACZ_RELOAD_STATE_ORDINAL, type.ordinal());
        self.getEntityData().set(MTACZ_RELOAD_COUNTDOWN, countdown);
    }

    @Override
    public void mtacz$clearReloadSync() {
        Mob self = (Mob) (Object) this;
        self.getEntityData().set(MTACZ_RELOADING, false);
        self.getEntityData().set(MTACZ_RELOAD_STATE_ORDINAL, ReloadState.StateType.NOT_RELOADING.ordinal());
        self.getEntityData().set(MTACZ_RELOAD_COUNTDOWN, 0);
    }

    @Override
    public void mtacz$setBoltingSync(boolean bolting) {
        Mob self = (Mob) (Object) this;
        self.getEntityData().set(MTACZ_BOLTING, bolting);
    }

    @Override
    public boolean mtacz$isReloadingSynced() {
        Mob self = (Mob) (Object) this;
        return self.getEntityData().get(MTACZ_RELOADING);
    }

    @Override
    public ReloadState.StateType mtacz$getReloadStateTypeSynced() {
        Mob self = (Mob) (Object) this;
        int ordinal = self.getEntityData().get(MTACZ_RELOAD_STATE_ORDINAL);
        ReloadState.StateType[] values = ReloadState.StateType.values();
        if (ordinal < 0 || ordinal >= values.length) {
            return ReloadState.StateType.NOT_RELOADING;
        }
        return values[ordinal];
    }

    @Override
    public int mtacz$getReloadCountdownSynced() {
        Mob self = (Mob) (Object) this;
        return self.getEntityData().get(MTACZ_RELOAD_COUNTDOWN);
    }

    @Override
    public boolean mtacz$isBoltingSynced() {
        Mob self = (Mob) (Object) this;
        return self.getEntityData().get(MTACZ_BOLTING);
    }
}