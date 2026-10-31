package com.vomiter.mobstacz.data.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import java.util.List;

public class ModLootTables extends LootTableProvider {
    public ModLootTables(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, Collections.emptySet(), List.of(
                new SubProviderEntry(provider -> new SubLootTable(), LootContextParamSets.ALL_PARAMS)
        ), lookupProvider);
    }
}
