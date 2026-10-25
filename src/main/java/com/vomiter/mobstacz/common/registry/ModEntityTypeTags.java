package com.vomiter.mobstacz.common.registry;

import com.vomiter.mobstacz.MobsTacz;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class ModEntityTypeTags {
    public static final TagKey<EntityType<?>> GUN_EQUIP_BLACKLIST = TagKey.create(
            Registries.ENTITY_TYPE, MobsTacz.modLoc("gun_equip_blacklist"));

    private ModEntityTypeTags() {
    }
}
