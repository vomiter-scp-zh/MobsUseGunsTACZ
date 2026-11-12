package com.vomiter.mobstacz.mixin.reload;

import com.tacz.guns.entity.shooter.LivingEntityAmmoCheck;
import com.vomiter.mobstacz.common.entity.MobGunUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityAmmoCheck.class, remap = false)
public abstract class LivingEntityAmmoCheckMixin {
    @Shadow
    @Final
    private LivingEntity shooter;

    @Inject(method = {"needCheckAmmo", "consumesAmmoOrNot"}, at = @At("HEAD"), cancellable = true)
    private void mtacz$resolveAmmoConsumption(CallbackInfoReturnable<Boolean> cir) {
        if (shooter instanceof Mob mob) {
            cir.setReturnValue(MobGunUtils.shouldConsumeAmmo(mob));
        }
    }
}
