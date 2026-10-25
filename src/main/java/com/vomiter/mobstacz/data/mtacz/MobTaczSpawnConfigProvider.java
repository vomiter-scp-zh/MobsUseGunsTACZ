package com.vomiter.mobstacz.data.mtacz;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class MobTaczSpawnConfigProvider implements DataProvider {
    private static final float BASIC = 1.5F;
    private static final float IMPROVED = 2.25F;
    private static final float RIFLE = 3.0F;
    private static final float BATTLE_RIFLE = 3.5F;
    private static final float ELITE = 4.5F;
    private final PackOutput.PathProvider paths;

    public MobTaczSpawnConfigProvider(PackOutput output) {
        paths = output.createPathProvider(PackOutput.Target.DATA_PACK, "mob_tacz");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> writes = new ArrayList<>();
        Set<ResourceLocation> ids = new HashSet<>();
        buildConfigs((id, config) -> {
            if (!ids.add(id)) throw new IllegalStateException("Duplicate mob_tacz config: " + id);
            writes.add(DataProvider.saveStable(output, config.toJson(), paths.json(id)));
        });
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    protected void buildConfigs(BiConsumer<ResourceLocation, MobTaczSpawnConfig> output) {
        adult(0.25)
                .gun("tacz:glock_17", 50, BASIC)
                .gun("tacz:cz75", 25, BASIC)
                .gun("tacz:uzi", 20, BASIC)
                .gun("tacz:m1911", 5, IMPROVED)
                .save(output, EntityType.ZOMBIE);
        adult(0.25)
                .gun("tacz:uzi", 40, BASIC)
                .gun("tacz:ump45", 35, IMPROVED)
                .gun("tacz:ak47", 20, RIFLE)
                .gun("tacz:db_long", 5, ELITE)
                .save(output, EntityType.HUSK);
        adult(0.05)
                .gun("tacz:glock_17", 80, BASIC)
                .gun("tacz:cz75", 20, BASIC)
                .save(output, EntityType.ZOMBIE_VILLAGER);
        adult(0.25)
                .gun("tacz:hk_mp5a5", 40, IMPROVED)
                .gun("tacz:sks_tactical", 35, IMPROVED)
                .gun("tacz:m1911", 20, IMPROVED)
                .gun("tacz:m700", 5, ELITE)
                .save(output, EntityType.SKELETON);
        adult(0.25)
                .gun("tacz:sks_tactical", 60, IMPROVED)
                .gun("tacz:m1911", 35, IMPROVED)
                .gun("tacz:m700", 5, ELITE)
                .save(output, EntityType.STRAY);
        adult(0.35)
                .gun("tacz:hk_mp5a5", 30, IMPROVED)
                .gun("tacz:m4a1", 35, RIFLE)
                .gun("tacz:ak47", 25, RIFLE)
                .gun("tacz:m16a4", 10, ELITE)
                .save(output, EntityType.PILLAGER);
        adult(0.3)
                .gun("tacz:deagle_golden", 45, BASIC)
                .gun("tacz:ump45", 35, IMPROVED)
                .gun("tacz:ak47", 20, RIFLE)
                .save(output, EntityType.PIGLIN);
        adult(0.3)
                .gun("tacz:fn_fal", 80, BATTLE_RIFLE)
                .gun("tacz:db_long", 20, ELITE)
                .save(output, EntityType.WITHER_SKELETON);
    }

    private static MobTaczSpawnConfigBuilder adult(double chance) {
        return MobTaczSpawnConfigBuilder.weighted(chance).allowBabies(false);
    }

    @Override
    public String getName() {
        return "MobsTacz mob gun spawn configs";
    }
}
