package com.vomiter.mobstacz;

import com.mojang.logging.LogUtils;
import com.tacz.guns.api.item.IGun;
import com.vomiter.mobstacz.common.event.EventHandler;
import com.vomiter.mobstacz.common.registry.ModRegistries;
import com.vomiter.mobstacz.data.ModDataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MobsTacz.MOD_ID)
public class MobsTacz
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "mobstacz";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation modLoc(String path){
        return Helpers.id(MobsTacz.MOD_ID, path);
    }

    public MobsTacz(FMLJavaModLoadingContext context) {
        EventHandler.init();
        IEventBus modBus = context.getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(ModDataGenerator::generateData);
        ModRegistries.register(modBus);
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

}
