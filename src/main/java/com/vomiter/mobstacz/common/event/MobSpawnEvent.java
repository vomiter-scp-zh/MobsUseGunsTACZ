package com.vomiter.mobstacz.common.event;

import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.vomiter.mobstacz.data.mtacz.MobTaczConfigManager;
import com.vomiter.mobstacz.data.mtacz.MobTaczSpawnConfig;
import com.vomiter.mobstacz.data.mtacz.MobTaczSpawnEntry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class MobSpawnEvent {
    public static void onFinalizeSpawn(net.minecraftforge.event.entity.living.MobSpawnEvent.FinalizeSpawn event) {
        Mob mob = event.getEntity();
        if (mob.level().isClientSide()) return;
        if (!mob.getMainHandItem().isEmpty()) return;

        var diffInstance = event.getDifficulty();
        if (diffInstance == null) return;
        float difficulty = diffInstance.getEffectiveDifficulty();

        MobTaczSpawnConfig config = MobTaczConfigManager.getSpawn(mob.getType());
        if (config == null || !config.hasEntries()) return;

        for (MobTaczSpawnEntry entry : config.guns()) {
            if (entry == null) continue;
            if (entry.gunId() == null) continue;
            if (difficulty < entry.minDifficulty()) continue;
            if (mob.getRandom().nextFloat() > entry.chance()) continue;
            ItemStack gun = GunItemBuilder.create()
                    .setId(entry.gunId())
                    .forceBuild();
            mob.setItemSlot(EquipmentSlot.MAINHAND, gun);
            mob.setDropChance(EquipmentSlot.MAINHAND, 0.085f);
            return;
        }
    }
}