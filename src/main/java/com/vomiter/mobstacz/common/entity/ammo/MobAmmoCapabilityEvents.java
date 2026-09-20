package com.vomiter.mobstacz.common.entity.ammo;

import com.vomiter.mobstacz.MobsTacz;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class MobAmmoCapabilityEvents {
    private static final int AMMO_INVENTORY_SIZE = 4;

    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Mob)) {
            return;
        }

        MobAmmoCapabilityProvider provider = new MobAmmoCapabilityProvider(AMMO_INVENTORY_SIZE);
        event.addCapability(MobsTacz.modLoc("mob_ammo"), provider);
        event.addListener(provider::invalidate);
    }
}