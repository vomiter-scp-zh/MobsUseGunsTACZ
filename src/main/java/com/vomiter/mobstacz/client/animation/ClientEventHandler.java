package com.vomiter.mobstacz.client.animation;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.event.common.GunReloadEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.config.common.GunConfig;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.sound.SoundManager;
import com.vomiter.mobstacz.MobsTacz;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;

public final class ClientEventHandler {

    public static void init(){
        var bus = NeoForge.EVENT_BUS;
        bus.addListener(ClientEventHandler::onGunReload);
    }

    public static void onGunReload(GunReloadEvent event) {
        if (event.getLogicalSide() != LogicalSide.CLIENT) {
            return;
        }

        // 避免攔到玩家自己的換彈，否則本機玩家會重複播放。
        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }

        ItemStack gunStack = event.getGunItemStack();
        IGun gun = IGun.getIGunOrNull(gunStack);
        if (gun == null) {
            return;
        }

        TimelessAPI.getClientGunIndex(gun.getGunId(gunStack))
                .ifPresent(gunIndex -> {
                    boolean emptyReload;

                    if (gunIndex.getGunData().getBolt() == Bolt.OPEN_BOLT) {
                        emptyReload =
                                gun.getCurrentAmmoCount(gunStack) <= 0;
                    } else {
                        emptyReload =
                                !gun.hasBulletInBarrel(gunStack);
                    }

                    TimelessAPI.getGunDisplay(gunStack)
                            .ifPresent(display -> {
                                String soundName = emptyReload
                                        ? SoundManager.RELOAD_EMPTY_SOUND
                                        : SoundManager.RELOAD_TACTICAL_SOUND;

                                SoundPlayManager.playClientSound(
                                        mob,
                                        display.getSounds(soundName),
                                        1.0F,
                                        1.0F,
                                        GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get()
                                );
                            });
                });
    }
}