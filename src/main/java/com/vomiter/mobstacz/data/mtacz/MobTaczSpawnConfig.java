package com.vomiter.mobstacz.data.mtacz;

import java.util.List;

public record MobTaczSpawnConfig(List<MobTaczSpawnEntry> guns) {
    public static final MobTaczSpawnConfig DEFAULT =
            new MobTaczSpawnConfig(List.of());

    public boolean hasEntries() {
        return guns != null && !guns.isEmpty();
    }
}