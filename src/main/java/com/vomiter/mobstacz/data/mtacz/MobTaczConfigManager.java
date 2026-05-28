package com.vomiter.mobstacz.data.mtacz;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MobTaczConfigManager {
    //private static final Map<ResourceLocation, MobShieldConfig> CONFIGS = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, MobTaczSpawnConfig> SPAWN_CONFIGS = new ConcurrentHashMap<>();

    private MobTaczConfigManager() {
    }

    public static void clear() {
        //CONFIGS.clear();
        SPAWN_CONFIGS.clear();
    }

    /*
    public static void put(ResourceLocation entityId, MobShieldConfig config) {
        CONFIGS.put(entityId, config);
    }

    public static MobShieldConfig get(ResourceLocation entityId) {
        return CONFIGS.getOrDefault(entityId, MobShieldConfig.DEFAULT);
    }

    public static MobShieldConfig get(EntityType<?> type) {
        ResourceLocation key = EntityType.getKey(type);
        return key != null ? get(key) : MobShieldConfig.DEFAULT;
    }
     */

    public static void put(ResourceLocation entityId, MobTaczSpawnConfig config) {
        SPAWN_CONFIGS.put(entityId, config);
    }


    public static MobTaczSpawnConfig getSpawn(ResourceLocation entityId) {
        return SPAWN_CONFIGS.getOrDefault(entityId, MobTaczSpawnConfig.DEFAULT);
    }

    public static MobTaczSpawnConfig getSpawn(EntityType<?> type) {
        ResourceLocation key = EntityType.getKey(type);
        return key != null ? getSpawn(key) : MobTaczSpawnConfig.DEFAULT;
    }
}