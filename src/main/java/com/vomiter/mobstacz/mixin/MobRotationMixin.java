package com.vomiter.mobstacz.mixin;

import com.vomiter.mobstacz.common.entity.ai.GunMode;
import com.vomiter.mobstacz.common.entity.ai.IMobGunState;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobRotationMixin {
    @Inject(method = "getMaxHeadYRot", at = @At("HEAD"), cancellable = true)
    private void mtacz$limitHeadBodyDifference(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof IMobGunState gunState
                && gunState.mtacz$getMode() == GunMode.RANGED) {
            cir.setReturnValue(10);
        }
    }
}
