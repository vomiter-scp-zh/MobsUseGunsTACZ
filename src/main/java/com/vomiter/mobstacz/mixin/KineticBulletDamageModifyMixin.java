package com.vomiter.mobstacz.mixin;

import com.tacz.guns.entity.EntityKineticBullet;
import com.vomiter.mobstacz.Config;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityKineticBullet.class, remap = false)
public class KineticBulletDamageModifyMixin {
    @Inject(method = "getDamage", at = @At("RETURN"), cancellable = true)
    private void mobstacz$setDamage(Vec3 hitVec, CallbackInfoReturnable<Float> cir){
        var self = (EntityKineticBullet)(Object)this;
        if(self.getOwner() instanceof Player) return;
        cir.setReturnValue((cir.getReturnValueF() * (float)Config.MOB_BULLET_DAMAGE_MULTIPLIER));
    }
}
