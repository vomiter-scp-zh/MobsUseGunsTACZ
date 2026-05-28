package com.vomiter.mobstacz.mixin;

import com.vomiter.mobstacz.common.entity.IAmmoStorage;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Mob.class)
public class MobAmmoStorageMixin implements IAmmoStorage {
    @Unique int mobstacz$ammoCount;
    @Unique boolean mobstacz$hasBasicAmmo = false;

    @Override
    public int mobstacz$getAmmoCount() {
        return mobstacz$ammoCount;
    }

    @Override
    public void mobstacz$setAmmoCount(int i) {
        mobstacz$ammoCount = i;
    }

    @Override
    public boolean mobstacz$hasBasicAmmo() {
        return mobstacz$hasBasicAmmo;
    }

    @Override
    public void mobstacz$setHasBasicAmmo(boolean b) {
        mobstacz$hasBasicAmmo = b;
    }
}
