package com.vomiter.mobstacz.common.entity;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.index.CommonAmmoIndex;
import com.vomiter.mobstacz.MobsTacz;
import com.vomiter.mobstacz.common.entity.ammo.ModCapabilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class MobGunUtils {
    public static boolean canReload(Mob shooter){
        return shooter.getMainHandItem().getItem() instanceof AbstractGunItem gunItem
                && gunItem.canReload(shooter, shooter.getMainHandItem());
    }

    private static String INITIAL_AMMO = "initial_ammo_given";
    public static boolean giveInitialAmmoStack(Mob mob, ItemStack gun){
        if (mob.getPersistentData().getBoolean(INITIAL_AMMO)) return false;
        mob.getPersistentData().putBoolean(INITIAL_AMMO, true);
        giveAmmoStack(mob, gun);
        return true;
    }

    private static void giveAmmoStack(Mob mob, ItemStack gunStack) {
        IGun gun = IGun.getIGunOrNull(gunStack);
        if (gun == null) {
            return;
        }

        ResourceLocation gunId = gun.getGunId(gunStack);

        var gunIndexOptional = TimelessAPI.getCommonGunIndex(gunId);
        if (gunIndexOptional.isEmpty()) {
            MobsTacz.LOGGER.warn(
                    "[MobsTacz] Cannot find gun index for {}",
                    gunId
            );
            return;
        }

        ResourceLocation ammoId = gunIndexOptional
                .get()
                .getGunData()
                .getAmmoId();

        int stackSize = TimelessAPI
                .getCommonAmmoIndex(ammoId)
                .map(CommonAmmoIndex::getStackSize)
                .orElse(1);

        ItemStack ammoStack = AmmoItemBuilder.create()
                .setId(ammoId)
                .setCount(stackSize)
                .build();

        ItemStack remainder = mob
                .getCapability(ModCapabilities.MOB_AMMO)
                .map(ammoInventory ->
                        ItemHandlerHelper.insertItemStacked(
                                ammoInventory,
                                ammoStack,
                                false
                        )
                )
                .orElse(ammoStack);

        if (!remainder.isEmpty()) {
            mob.spawnAtLocation(remainder);
        }
    }

}
