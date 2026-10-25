package com.vomiter.mobstacz.common.event;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.vomiter.mobstacz.MobsTacz;
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

        int startIndex = 0;
        while (startIndex < config.guns().size()) {
            MobTaczSpawnEntry entry = config.select(difficulty, mob.getRandom(), candidate -> {
                if (TimelessAPI.getCommonGunIndex(candidate.gunId()).isPresent()) return true;
                MobsTacz.LOGGER.warn("[MobsTacz] unknown TACZ gun id: {}", candidate.gunId());
                return false;
            }, startIndex);
            if (entry == null) return;
            startIndex += config.guns().subList(startIndex, config.guns().size()).indexOf(entry) + 1;
            try {
                var gunIndexOptional = TimelessAPI.getCommonGunIndex(entry.gunId());

                if (gunIndexOptional.isEmpty()) {
                    MobsTacz.LOGGER.warn("[MobsTacz] unknown TACZ gun id: {}", entry.gunId());
                    return;
                }

                var gunIndex = gunIndexOptional.get();
                var gunData = gunIndex.getGunData();

                ItemStack gun = GunItemBuilder.create()
                        .setId(entry.gunId())
                        .setAmmoCount(gunData.getAmmoAmount())
                        .setFireMode(gunData.getFireModeSet().get(0))
                        .setAmmoInBarrel(true)
                        .build();

                if (gun.isEmpty()) {
                    MobsTacz.LOGGER.warn("[MobsTacz] failed to build gun item from TACZ index: {}", entry.gunId());
                    return;
                }

                mob.setItemSlot(EquipmentSlot.MAINHAND, gun);
                mob.setDropChance(EquipmentSlot.MAINHAND, 0.085f);
                return;

            } catch (Exception e) {
                MobsTacz.LOGGER.error("[MobsTacz] unable to build gun: {}", entry.gunId(), e);
                if (config.isWeighted()) return;
                // Legacy configs used to continue checking later entries after a build exception.
            }
        }
    }
}