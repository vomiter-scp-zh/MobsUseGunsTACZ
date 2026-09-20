package com.vomiter.mobstacz.mixin.reload;

import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.vomiter.mobstacz.common.entity.ammo.ModCapabilities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public abstract class ModernKineticGunScriptAPIMixin {
    @Shadow
    private AbstractGunItem abstractGunItem;

    @Shadow
    private ItemStack itemStack;

    @Shadow
    public abstract boolean isReloadingNeedConsumeAmmo();

    @Shadow
    private LivingEntity shooter;

    @Inject(method = "consumeAmmoFromPlayer", at = @At("RETURN"), cancellable = true)
    private void mtacz$consumeAmmoFromPlayer(int neededAmount, CallbackInfoReturnable<Integer> cir){
        if (cir.getReturnValue() < neededAmount){
            if (!(this.shooter instanceof Mob mob)) return;
            int missingCount = neededAmount - cir.getReturnValue();
            if (mob.level().isClientSide) {
            }
            
            int backpackAvailable = mob.getCapability(ModCapabilities.MOB_AMMO).map(
                    iMobAmmoHandler -> {
                        int found = 0;
                        for (int i = 0; i < iMobAmmoHandler.getSlots(); i++) {
                            if (found >= missingCount) return found;
                            int toDraw = missingCount - found;
                            ItemStack ammoStack = iMobAmmoHandler.getStackInSlot(i);
                            if (ammoStack.getItem() instanceof IAmmo ammo
                                    && ammo.isAmmoOfGun(itemStack, ammoStack)) {
                                ItemStack extracted = iMobAmmoHandler.extractItem(i, toDraw, false);
                                found += extracted.getCount();
                            } else if (ammoStack.getItem() instanceof IAmmoBox iAmmoBox && iAmmoBox.isAmmoBoxOfGun(itemStack, ammoStack)) {
                                found += Math.min(iAmmoBox.getAmmoCount(ammoStack), toDraw);
                                iAmmoBox.setAmmoCount(ammoStack, iAmmoBox.getAmmoCount(ammoStack) - Math.min(iAmmoBox.getAmmoCount(ammoStack), toDraw));
                            }
                        }
                        return found;
                    }
            ).orElse(0);

            if(backpackAvailable > 0){
                cir.setReturnValue(cir.getReturnValue() + backpackAvailable);
            }
        }
    }


    @Inject(
            method = "hasAmmoToConsume",
            at = @At("RETURN"),
            cancellable = true
    )
    private void mtacz$hasAmmoToConsume(
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (cir.getReturnValue()) {
            return;
        }

        if (!(shooter instanceof Mob mob)) {
            return;
        }

        boolean hasAmmo = mob
                .getCapability(ModCapabilities.MOB_AMMO)
                .map(ammoInventory -> {
                    for (int slot = 0; slot < ammoInventory.getSlots(); slot++) {
                        ItemStack ammoStack = ammoInventory.getStackInSlot(slot);

                        if (ammoStack.getItem() instanceof IAmmo ammo
                                && ammo.isAmmoOfGun(itemStack, ammoStack)) {
                            return true;
                        }

                        if (ammoStack.getItem() instanceof IAmmoBox ammoBox
                                && ammoBox.isAmmoBoxOfGun(itemStack, ammoStack)
                                && ammoBox.getAmmoCount(ammoStack) > 0) {
                            return true;
                        }
                    }

                    return false;
                })
                .orElse(false);

        if (hasAmmo) {
            cir.setReturnValue(true);
        }
    }
}
