package com.vomiter.mobstacz.common.event;

import com.vomiter.mobstacz.common.command.ModCommand;
import com.vomiter.mobstacz.data.mtacz.MobTaczDataConfigReloadListener;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.EventPriority;

public class EventHandler {
    public static void init(){
        final IEventBus bus = NeoForge.EVENT_BUS;
        bus.addListener(EventHandler::onRegisterCommands);
        bus.addListener(EquipGunEvent::onMobEquipGun);
        bus.addListener(MobTaczDataConfigReloadListener::onAddReloadListeners);
        bus.addListener(MobSpawnEvent::onFinalizeSpawn);
        bus.addListener(EventPriority.HIGHEST, MobSpawnEvent::onEntityJoinLevel);
        bus.addListener(DeathEvent::onLivingDeath);

    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommand.register(event.getDispatcher());
    }




}
