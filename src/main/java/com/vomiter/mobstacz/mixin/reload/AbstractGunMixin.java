package com.vomiter.mobstacz.mixin.reload;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.util.AttachmentDataUtils;
import com.vomiter.mobstacz.common.entity.ammo.ModCapabilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractGunItem.class, remap = false)
public abstract class AbstractGunMixin  implements IGun {
    @Inject(method = "canReload", at = @At("RETURN"), cancellable = true)
    private void mtacz$canReload(
            LivingEntity shooter, ItemStack gunItem, CallbackInfoReturnable<Boolean> cir){
        ResourceLocation gunId = this.getGunId(gunItem);
        if (cir.getReturnValue()) return;
        if (!(shooter instanceof Mob mob)) return;
        CommonGunIndex gunIndex = TimelessAPI.getCommonGunIndex(gunId).orElse(null);
        if (gunIndex == null) {
            return;
        } else {
            int currentAmmoCount = this.getCurrentAmmoCount(gunItem);
            int maxAmmoCount = AttachmentDataUtils.getAmmoCountWithAttachment(gunItem, gunIndex.getGunData());
            if (currentAmmoCount >= maxAmmoCount) {
                return;
            } else if (this.useInventoryAmmo(gunItem)) {
                return;
            } else if (gunIndex.getGunData().getReloadData().isInfinite()) {
                return;
            } else if (this.useDummyAmmo(gunItem)) {
                return;
            } else {
                if (mob.level().isClientSide) {
                }

                int backpackAvailable = mob.getCapability(ModCapabilities.MOB_AMMO).map(
                        iMobAmmoHandler -> {
                            int found = 0;
                            for (int i = 0; i < iMobAmmoHandler.getSlots(); i++) {
                                ItemStack ammoStack = iMobAmmoHandler.getStackInSlot(i);
                                if (ammoStack.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(gunItem, ammoStack)){
                                    found += ammoStack.getCount();
                                } else if (ammoStack.getItem() instanceof IAmmoBox iAmmoBox && iAmmoBox.isAmmoBoxOfGun(gunItem, ammoStack)) {
                                    found += iAmmoBox.getAmmoCount(ammoStack);
                                }
                            }
                            return found;
                        }
                ).orElse(0);
                if (backpackAvailable > 0) cir.setReturnValue(true);
            }
        }

    }

    @Inject(method = "hasInventoryAmmo", at = @At("RETURN"), cancellable = true)
    private void mtacz$hasInventoryAmmo(
            LivingEntity shooter, ItemStack gunItem, boolean needCheckAmmo, CallbackInfoReturnable<Boolean> cir){
        ResourceLocation gunId = this.getGunId(gunItem);
        if (cir.getReturnValue()) return;
        if (!(shooter instanceof Mob mob)) return;
        CommonGunIndex gunIndex = TimelessAPI.getCommonGunIndex(gunId).orElse(null);
        if (gunIndex == null) {
            return;
        } else {
            if (!this.useInventoryAmmo(gunItem)) {
                return;
            }
            if (this.useDummyAmmo(gunItem)) {
                return;
            }
            else {
                if (mob.level().isClientSide) {
                }

                int backpackAvailable = mob.getCapability(ModCapabilities.MOB_AMMO).map(
                        iMobAmmoHandler -> {
                            int found = 0;
                            for (int i = 0; i < iMobAmmoHandler.getSlots(); i++) {
                                ItemStack ammoStack = iMobAmmoHandler.getStackInSlot(i);
                                if (ammoStack.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(gunItem, ammoStack)){
                                    found += ammoStack.getCount();
                                } else if (ammoStack.getItem() instanceof IAmmoBox iAmmoBox && iAmmoBox.isAmmoBoxOfGun(gunItem, ammoStack)) {
                                    found += iAmmoBox.getAmmoCount(ammoStack);
                                }
                            }
                            return found;
                        }
                ).orElse(0);
                if (backpackAvailable > 0) cir.setReturnValue(true);
            }
        }

    }

}
