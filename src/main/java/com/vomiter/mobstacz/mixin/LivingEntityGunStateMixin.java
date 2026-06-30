package com.vomiter.mobstacz.mixin;

import com.tacz.guns.api.item.IGun;
import com.vomiter.mobstacz.common.entity.MobGunAnimationSyncHelper;
import com.vomiter.mobstacz.common.entity.ai.GunMode;
import com.vomiter.mobstacz.common.entity.ai.IMobGunState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityGunStateMixin implements IMobGunState {
    @Inject(method = "tick", at = @At("TAIL"))
    private void mtacz$tickDecayOffset(CallbackInfo ci){
        if((Object)this instanceof Mob mob){
            if(IGun.getIGunOrNull(mob.getMainHandItem()) != null) MobGunAnimationSyncHelper.syncFromOperator(mob);
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
