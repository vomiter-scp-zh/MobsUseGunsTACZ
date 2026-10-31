package com.vomiter.mobstacz.common.event;

import com.vomiter.mobstacz.common.entity.ammo.ModCapabilities;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class DeathEvent {
    static void onLivingDeath(LivingDeathEvent event){
        var entity = event.getEntity();
        if (entity instanceof Mob mob){
            List<ItemStack> ammoStacks = new ArrayList<>();
            ModCapabilities.getMobAmmo(mob)
                .map(ammoInventory -> {
                    for (int i = 0; i < ammoInventory.getSlots(); i++) {
                        int count = 0;
                        for (int i1 = 0; i1 < ammoInventory.getStackInSlot(i).getCount(); i1++) {
                            if (mob.getRandom().nextFloat() < 0.3){
                                count++;
                            }
                        }
                        ammoStacks.add(ammoInventory.extractItem(i, count, false));
                    }
                    return true;
                }
                )
                .orElse(false);
            ammoStacks.forEach(mob::spawnAtLocation);
        }
    }
}
