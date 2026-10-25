package com.vomiter.mobstacz.common.entity.spawn;

import com.vomiter.mobstacz.data.mtacz.MobTaczSpawnConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

/** Temporary per-entity protection between the gun decision and entering the level. */
public final class SpawnGunEquipment {
    private ItemStack selectedGun;
    private MobTaczSpawnConfig config;

    public void select(ItemStack selectedGun, MobTaczSpawnConfig config) {
        this.selectedGun = selectedGun;
        this.config = config;
    }

    public boolean blocksReplacement(Mob mob, EquipmentSlot slot, ItemStack incoming) {
        if (selectedGun == null || slot != EquipmentSlot.MAINHAND
                || mob.getMainHandItem() != selectedGun) return false;
        // Baby age is often decided inside finalizeSpawn, after the initial gun roll.
        if (!config.allowBabies() && mob.isBaby()) return false;
        if (config.preserves(incoming)) {
            clear();
            return false;
        }
        return incoming != selectedGun;
    }

    public void finish(Mob mob) {
        if (selectedGun == null) return;
        boolean stillSelected = mob.getMainHandItem() == selectedGun;
        boolean excludedBaby = !config.allowBabies() && mob.isBaby();
        clear();
        // Clear the guard before writing the slot, so cleanup cannot block itself.
        if (stillSelected && excludedBaby) mob.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        else if (stillSelected) mob.setDropChance(EquipmentSlot.MAINHAND, 0.085F);
    }

    private void clear() {
        selectedGun = null;
        config = null;
    }
}
