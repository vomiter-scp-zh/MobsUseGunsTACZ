package com.vomiter.mobstacz.data.mtacz;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public record MobTaczSpawnConfig(List<MobTaczSpawnEntry> guns, double spawnChance) {
    public static final MobTaczSpawnConfig DEFAULT = new MobTaczSpawnConfig(List.of());

    public MobTaczSpawnConfig(List<MobTaczSpawnEntry> guns) {
        this(guns, 1.0D);
    }

    public MobTaczSpawnConfig {
        guns = List.copyOf(guns);
        if (!Double.isFinite(spawnChance) || spawnChance < 0 || spawnChance > 1) {
            throw new IllegalArgumentException("spawn_chance must be finite and within [0, 1]");
        }
        boolean weighted = guns.stream().anyMatch(entry -> entry.weight() != null);
        if (weighted && guns.stream().anyMatch(entry -> entry.weight() == null)) {
            throw new IllegalArgumentException("All guns must specify weight when using weighted selection");
        }
    }

    public boolean hasEntries() {
        return !guns.isEmpty();
    }

    public boolean isWeighted() {
        return hasEntries() && guns.get(0).weight() != null;
    }

    /** One draw, with the unassigned interval representing no gun. */
    public MobTaczSpawnEntry select(float difficulty, RandomSource random,
                                    Predicate<MobTaczSpawnEntry> available) {
        return select(difficulty, random, available, 0);
    }

    /** startIndex preserves the legacy continue-after-build-exception behavior. */
    public MobTaczSpawnEntry select(float difficulty, RandomSource random,
                                    Predicate<MobTaczSpawnEntry> available, int startIndex) {
        List<MobTaczSpawnEntry> candidates = new ArrayList<>();
        for (int i = startIndex; i < guns.size(); i++) {
            MobTaczSpawnEntry entry = guns.get(i);
            if (entry.gunId() == null || difficulty < entry.minDifficulty()) continue;
            if (isWeighted() && entry.weight() == 0) continue;
            if (available.test(entry)) candidates.add(entry);
        }
        if (candidates.isEmpty() || spawnChance == 0) return null;

        double roll = random.nextDouble();
        if (roll >= spawnChance) return null;
        double[] weights = new double[candidates.size()];
        if (isWeighted()) {
            // Scaling first prevents overflow even with multiple Double.MAX_VALUE weights.
            double max = candidates.stream().mapToDouble(entry -> entry.weight()).max().orElseThrow();
            double total = 0;
            for (int i = 0; i < weights.length; i++) {
                weights[i] = candidates.get(i).weight() / max;
                total += weights[i];
            }
            double needle = roll / spawnChance * total;
            for (int i = 0; i < weights.length; i++) {
                if (needle < weights[i]) return candidates.get(i);
                needle -= weights[i];
            }
            // Rounding at the top edge must not introduce an unintended no-gun outcome.
            return candidates.get(candidates.size() - 1);
        } else {
            double remaining = 1;
            for (int i = 0; i < weights.length; i++) {
                float chance = candidates.get(i).chance();
                // Preserve old comparison semantics for out-of-range legacy values.
                double probability = Float.isNaN(chance) ? 1 : Math.max(0, Math.min(1, chance));
                weights[i] = remaining * probability * spawnChance;
                remaining *= 1 - probability;
            }
        }

        double cumulative = 0;
        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (roll < cumulative) return candidates.get(i);
        }
        return null;
    }

    public static MobTaczSpawnConfig parse(JsonObject obj) {
        List<MobTaczSpawnEntry> entries = new ArrayList<>();
        if (obj.has("guns")) {
            JsonArray array = GsonHelper.getAsJsonArray(obj, "guns");
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    throw new IllegalArgumentException("'guns' must contain only JSON objects");
                }
                entries.add(parseEntry(element.getAsJsonObject()));
            }
        } else if (obj.has("gunId")) {
            entries.add(parseEntry(obj));
        }
        return new MobTaczSpawnConfig(entries, GsonHelper.getAsDouble(obj, "spawn_chance", 1.0D));
    }

    private static MobTaczSpawnEntry parseEntry(JsonObject obj) {
        if (obj.has("weight") && obj.has("chance")) {
            throw new IllegalArgumentException("Use either weight or legacy chance in a gun entry, not both");
        }
        return new MobTaczSpawnEntry(
                ResourceLocation.tryParse(GsonHelper.getAsString(obj, "gunId")),
                GsonHelper.getAsFloat(obj, "chance", 0),
                GsonHelper.getAsFloat(obj, "min_difficulty", 2.25F),
                obj.has("weight") ? GsonHelper.getAsDouble(obj, "weight") : null
        );
    }
}
