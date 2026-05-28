package com.vomiter.mobstacz.common.gun;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunReloadData;
import com.vomiter.mobstacz.MobsTacz;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public final class TaczReloadHelper {
    private TaczReloadHelper() {
    }

    public static long getReloadPhaseMaxMillis(ItemStack gunStack, ReloadState.StateType stateType) {
        if (gunStack.isEmpty()) {
            MobsTacz.LOGGER.warn("[MTACZ] gunStack is empty");
            return 0L;
        }

        return TimelessAPI.getCommonGunIndex(Objects.requireNonNull(IGun.getIGunOrNull(gunStack)).getGunId(gunStack)).map(index -> {
            GunData gunData = index.getGunData();
            GunReloadData reloadData = gunData.getReloadData();
            if (reloadData == null) {
                MobsTacz.LOGGER.warn("[MTACZ] data is null");
                return 0L;
            }

            return switch (stateType) {
                case EMPTY_RELOAD_FEEDING ->
                        (long) (reloadData.getFeed().getEmptyTime() * 1000L);
                case EMPTY_RELOAD_FINISHING ->
                        (long) (reloadData.getCooldown().getEmptyTime() * 1000L);
                case TACTICAL_RELOAD_FEEDING ->
                        (long) (reloadData.getFeed().getTacticalTime() * 1000L);
                case TACTICAL_RELOAD_FINISHING ->
                        (long) (reloadData.getCooldown().getTacticalTime() * 1000L);
                default -> 0L;
            };
        }).orElse(-100L);
    }

    public static float getReloadPhaseProgress(ItemStack gunStack, ReloadState reloadState) {
        if (reloadState == null) {
            MobsTacz.LOGGER.warn("[MTACZ] RELOAD STATE = NULL");
            return 0.0F;
        }

        long max = getReloadPhaseMaxMillis(gunStack, reloadState.getStateType());
        if (max <= 0L) {
            MobsTacz.LOGGER.warn("[MTACZ] RELOAD MAX {} < 0", max);
            return 0.0F;
        }

        long countDown = reloadState.getCountDown();
        float progress = 1.0F - (countDown / (float) max);

        if (progress < 0.0F) return 0.0F;
        if (progress > 1.0F) return 1.0F;
        return progress;
    }
}