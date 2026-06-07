package com.vomiter.mobstacz.mixin;

import com.vomiter.mobstacz.common.entity.ai.GunMode;
import com.vomiter.mobstacz.common.entity.ai.IGunState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public class LivingEntityGunStateMixin implements IGunState {
    @Unique
    private GunMode mode;

    @Unique
    private float aimYawOffset;

    @Unique
    private float aimPitchOffset;


    @Unique
    public GunMode mtacz$getMode() {
        return mode;
    }

    @Unique
    public void mtacz$setMode(GunMode mode) {
        this.mode = mode;
    }

    @Unique
    public float mtacz$getAimYawOffset() {
        return aimYawOffset;
    }

    @Unique
    public void mtacz$setAimYawOffset(float aimYawOffset) {
        this.aimYawOffset = aimYawOffset;
    }

    @Unique
    public float mtacz$getAimPitchOffset() {
        return aimPitchOffset;
    }

    @Unique
    public void mtacz$setAimPitchOffset(float aimPitchOffset) {
        this.aimPitchOffset = aimPitchOffset;
    }

}
