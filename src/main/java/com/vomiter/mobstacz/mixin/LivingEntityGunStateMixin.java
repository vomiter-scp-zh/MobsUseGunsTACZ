package com.vomiter.mobstacz.mixin;

import com.vomiter.mobstacz.common.entity.ai.GunMode;
import com.vomiter.mobstacz.common.entity.ai.IGunState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityGunStateMixin implements IGunState {
    @Inject(method = "tick", at = @At("TAIL"))
    private void mtacz$tickDecayOffset(CallbackInfo ci){
        var shooter = (LivingEntity)(Object)this;
        var shooterState = (IGunState)this;
        if((Object)this instanceof Player) return;
        if(shooter.tickCount % 10 == 0) {
            shooterState.mtacz$decayAimDrift();
        }
    }

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
