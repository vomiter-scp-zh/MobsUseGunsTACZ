package com.vomiter.mobstacz.data.mtacz;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/** Builder for weighted mob_tacz configs; runtime validation is shared with the loader. */
public final class MobTaczSpawnConfigBuilder {
    private final double spawnChance;
    private final List<MobTaczSpawnEntry> guns = new ArrayList<>();
    private final Set<ResourceLocation> preservedItems = new HashSet<>();
    private boolean allowBabies = true;

    private MobTaczSpawnConfigBuilder(double spawnChance) {
        this.spawnChance = spawnChance;
    }

    public static MobTaczSpawnConfigBuilder weighted(double spawnChance) {
        return new MobTaczSpawnConfigBuilder(spawnChance);
    }

    public MobTaczSpawnConfigBuilder allowBabies(boolean allowBabies) {
        this.allowBabies = allowBabies;
        return this;
    }

    public MobTaczSpawnConfigBuilder preserve(Item item) {
        preservedItems.add(BuiltInRegistries.ITEM.getKey(item));
        return this;
    }

    public MobTaczSpawnConfigBuilder gun(String gunId, double weight, float minDifficulty) {
        ResourceLocation id = ResourceLocation.tryParse(gunId);
        if (id == null) throw new IllegalArgumentException("Invalid gun ID: " + gunId);
        return gun(id, weight, minDifficulty);
    }

    public MobTaczSpawnConfigBuilder gun(ResourceLocation gunId, double weight, float minDifficulty) {
        if (gunId == null) throw new IllegalArgumentException("Gun ID cannot be null");
        if (!Float.isFinite(minDifficulty) || minDifficulty < 0) {
            throw new IllegalArgumentException("min_difficulty must be finite and nonnegative");
        }
        guns.add(new MobTaczSpawnEntry(gunId, 0, minDifficulty, weight));
        return this;
    }

    public MobTaczSpawnConfig build() {
        return new MobTaczSpawnConfig(guns, spawnChance, allowBabies, preservedItems);
    }

    public void save(BiConsumer<ResourceLocation, MobTaczSpawnConfig> output, EntityType<?> type) {
        output.accept(EntityType.getKey(type), build());
    }
}
