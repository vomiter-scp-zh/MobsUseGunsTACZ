package com.vomiter.mobstacz.data.mtacz;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vomiter.mobstacz.MobsTacz;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.jetbrains.annotations.NotNull;

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
        MobTaczConfigManager.clear();
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

                MobTaczSpawnConfig spawnConfig = MobTaczSpawnConfig.parse(obj);
                MobTaczMovementConfig movementConfig = obj.has("movement")
                        ? MobTaczMovementConfig.parse(GsonHelper.getAsJsonObject(obj, "movement"))
                        : MobTaczMovementConfig.DEFAULT;
                //MobTaczConfigManager.put(fileId, config);
                MobTaczConfigManager.put(fileId, spawnConfig);
                MobTaczConfigManager.putMovement(fileId, movementConfig);


            } catch (Exception e) {
                MobsTacz.LOGGER.error("[MTACZ] Failed to load mob tacz config {}", fileId, e);
            }
        }

        MobsTacz.LOGGER.info("[MTacz] Loaded {} mob tacz configs", map.size());
    }

}
