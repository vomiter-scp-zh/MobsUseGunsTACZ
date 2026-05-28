package com.vomiter.mobstacz.data.mtacz;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.vomiter.mobstacz.MobsTacz;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MobTaczDataConfigReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "mob_tacz";
    public static final MobTaczDataConfigReloadListener INSTANCE = new MobTaczDataConfigReloadListener();

    private MobTaczDataConfigReloadListener() {
        super(GSON, DIRECTORY);
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(MobTaczDataConfigReloadListener.INSTANCE);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation fileId = entry.getKey();
            JsonElement json = entry.getValue();

            try {
                if (!json.isJsonObject()) {
                    throw new IllegalArgumentException("Expected JSON object");
                }

                JsonObject obj = json.getAsJsonObject();

                //int useDuration = GsonHelper.getAsInt(obj, "use_duration", 60);
                //int cooldownDuration = GsonHelper.getAsInt(obj, "cooldown_duration", 60);
                //int checkInterval = GsonHelper.getAsInt(obj, "check_continue_to_use_interval", 30);

                MobTaczSpawnConfig spawnConfig = parseSpawnConfig(obj);
                //MobTaczConfigManager.put(fileId, config);
                MobTaczConfigManager.put(fileId, spawnConfig);


            } catch (Exception e) {
                MobsTacz.LOGGER.error("[MTACZ] Failed to load mob tacz config {}", fileId, e);
            }
        }

        MobsTacz.LOGGER.info("[MTacz] Loaded {} mob tacz configs", map.size());
    }

    private static MobTaczSpawnConfig parseSpawnConfig(JsonObject obj) {
        if (obj.has("guns")) {
            JsonArray arr = GsonHelper.getAsJsonArray(obj, "guns");
            List<MobTaczSpawnEntry> entries = new ArrayList<>();

            for (JsonElement element : arr) {
                if (!element.isJsonObject()) {
                    throw new IllegalArgumentException("'guns' must contain only JSON objects");
                }
                entries.add(parseSpawnEntry(element.getAsJsonObject()));
            }

            if (!entries.isEmpty()) {
                return new MobTaczSpawnConfig(List.copyOf(entries));
            }
        }

        // fallback: legacy single-entry fields
        return new MobTaczSpawnConfig(List.of(parseSpawnEntry(obj)));
    }

    private static MobTaczSpawnEntry parseSpawnEntry(JsonObject obj) {
        String gunId = GsonHelper.getAsString(obj, "gunId");
        float gunChance = GsonHelper.getAsFloat(obj, "chance", 0);
        float minDifficulty = GsonHelper.getAsFloat(obj, "min_difficulty", 2.25f);

        return new MobTaczSpawnEntry(
                ResourceLocation.tryParse(gunId),
                gunChance,
                minDifficulty
        );
    }
}