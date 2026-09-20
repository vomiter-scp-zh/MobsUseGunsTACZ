package com.vomiter.mobstacz.common.entity.ammo;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;

public final class ModCapabilities {
    public static final Capability<IMobAmmoHandler> MOB_AMMO = CapabilityManager.get(new CapabilityToken<>() {});

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IMobAmmoHandler.class);
    }

    public static LazyOptional<IMobAmmoHandler> getMobAmmo(Entity entity) {
        return entity.getCapability(MOB_AMMO);
    }
}