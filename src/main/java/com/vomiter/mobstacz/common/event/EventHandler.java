package com.vomiter.mobstacz.common.event;

import com.vomiter.mobstacz.common.command.ModCommand;
import com.vomiter.mobstacz.common.entity.ammo.MobAmmoCapabilityEvents;
import com.vomiter.mobstacz.data.mtacz.MobTaczDataConfigReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class EventHandler {
    public static void init(){
        final IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(EventHandler::onRegisterCommands);
        bus.addListener(EquipGunEvent::onMobEquipGun);
        bus.addListener(MobTaczDataConfigReloadListener::onAddReloadListeners);
        bus.addListener(MobSpawnEvent::onFinalizeSpawn);
        bus.addGenericListener(Entity.class, MobAmmoCapabilityEvents::onAttachCapabilities);

    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommand.register(event.getDispatcher());
    }




}
