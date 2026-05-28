package com.vomiter.mobstacz.data.mtacz;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record MobTaczSpawnEntry(ResourceLocation gunId, float chance, float minDifficulty) {
}