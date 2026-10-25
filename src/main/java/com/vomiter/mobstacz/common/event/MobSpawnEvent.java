package com.vomiter.mobstacz.common.event;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.vomiter.mobstacz.MobsTacz;
import com.vomiter.mobstacz.common.entity.spawn.SpawnGunEquipmentAccess;
import com.vomiter.mobstacz.data.mtacz.MobTaczConfigManager;
import com.vomiter.mobstacz.data.mtacz.MobTaczSpawnConfig;
import com.vomiter.mobstacz.data.mtacz.MobTaczSpawnEntry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

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
        if (!config.allowBabies() && mob.isBaby()) return;

        int startIndex = 0;
        while (startIndex < config.guns().size()) {
            MobTaczSpawnEntry entry = config.select(difficulty, mob.getRandom(), candidate -> {
                if (TimelessAPI.getCommonGunIndex(candidate.gunId()).isPresent()) return true;
                MobsTacz.LOGGER.warn("[MobsTacz] unknown TACZ gun id: {}", candidate.gunId());
                return false;
            }, startIndex);
            if (entry == null) return;
            while (startIndex < config.guns().size()) {
                if (config.guns().get(startIndex++) == entry) break;
            }
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
                ((SpawnGunEquipmentAccess) mob).mtacz$getSpawnGunEquipment().select(gun, config);
                return;

            } catch (Exception e) {
                MobsTacz.LOGGER.error("[MobsTacz] unable to build gun: {}", entry.gunId(), e);
                if (config.isWeighted()) return;
                // Legacy configs used to continue checking later entries after a build exception.
            }
        }
    }
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Mob mob)) return;
        ((SpawnGunEquipmentAccess) mob).mtacz$getSpawnGunEquipment().finish(mob);
    }
}
