package com.vomiter.mobstacz.mixin;

import com.tacz.guns.entity.shooter.LivingEntityAmmoCheck;
import com.vomiter.mobstacz.common.entity.IAmmoStorage;
import com.vomiter.mobstacz.common.entity.IMobGunAnimationSync;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityAmmoCheck.class, remap = false)
public class LivingEntityAmmoCheckMixin {
    @Shadow
    @Final
    private LivingEntity shooter;

    @Inject(method = "needCheckAmmo", at = @At("RETURN"), cancellable = true)
    private void bypassAmmoCheck(CallbackInfoReturnable<Boolean> cir){
        if(shooter instanceof IAmmoStorage storage && shooter instanceof IMobGunAnimationSync sync) {
            if(storage.mobstacz$getAmmoCount() > 0){
                cir.setReturnValue(false);
            }
        }
    }

}
