package com.vomiter.mobstacz.common.entity.ai;

import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Unique;

public interface IGunState {
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
        mtacz$setAimPitchOffset(Mth.clamp(a, b, mtacz$getAimPitchOffset()));
    }
    default void mtacz$clampYawOffset(float a, float b){
        mtacz$setAimYawOffset(Mth.clamp(a, b, mtacz$getAimYawOffset()));
    }
    default void mtacz$decayAimDrift() {
        mtacz$setAimPitchOffset(approachZero(mtacz$getAimPitchOffset(), 0.35F));
        mtacz$setAimYawOffset(approachZero(mtacz$getAimYawOffset(), 0.25F));
    }

    private float approachZero(float value, float amount) {
        if (value > 0) return Math.max(0, value - amount);
        if (value < 0) return Math.min(0, value + amount);
        return 0;
    }


}
