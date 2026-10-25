package com.vomiter.mobstacz.data.mtacz;

import net.minecraft.resources.ResourceLocation;

/** A null weight identifies a legacy per-entry chance. Zero weight disables an entry. */
public record MobTaczSpawnEntry(ResourceLocation gunId, float chance, float minDifficulty, Double weight) {
    public MobTaczSpawnEntry(ResourceLocation gunId, float chance, float minDifficulty) {
        this(gunId, chance, minDifficulty, null);
    }

    public MobTaczSpawnEntry {
        if (weight != null && (!Double.isFinite(weight) || weight < 0)) {
            throw new IllegalArgumentException("Gun weight must be finite and nonnegative");
        }
    }
}
