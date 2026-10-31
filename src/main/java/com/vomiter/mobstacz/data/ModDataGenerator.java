package com.vomiter.mobstacz.data;

import com.vomiter.mobstacz.data.loot.ModLootTables;
import com.vomiter.mobstacz.data.mtacz.MobTaczSpawnConfigProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class ModDataGenerator {
    public static void generateData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, helper));
        generator.addProvider(event.includeServer(), new ModLootTables(output, lookupProvider));
        generator.addProvider(event.includeServer(), new MobTaczSpawnConfigProvider(output));
        new ModTagProviders(event);
    }
}
