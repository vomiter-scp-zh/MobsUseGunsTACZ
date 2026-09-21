package com.vomiter.mobstacz.mixin;

import com.vomiter.mobstacz.common.entity.ai.GunMode;
import com.vomiter.mobstacz.common.entity.ai.IMobGunState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public class LivingEntityGunStateMixin implements IMobGunState {

    @Unique
    private GunMode mode;

    @Unique
    private float mtacz$aimYawOffset;

    @Unique
    private float mtacz$aimPitchOffset;


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
        return mtacz$aimYawOffset;
    }

    @Unique
    public void mtacz$setAimYawOffset(float aimYawOffset) {
        this.mtacz$aimYawOffset = aimYawOffset;
    }

    @Unique
    public float mtacz$getAimPitchOffset() {
        return mtacz$aimPitchOffset;
    }

    @Unique
    public void mtacz$setAimPitchOffset(float aimPitchOffset) {
        this.mtacz$aimPitchOffset = aimPitchOffset;
    }

}
