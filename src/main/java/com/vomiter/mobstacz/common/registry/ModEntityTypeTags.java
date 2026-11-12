package com.vomiter.mobstacz.common.registry;

import com.vomiter.mobstacz.MobsTacz;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class ModEntityTypeTags {
    public static final TagKey<EntityType<?>> GUN_EQUIP_BLACKLIST = TagKey.create(
            Registries.ENTITY_TYPE, MobsTacz.modLoc("gun_equip_blacklist"));

    public static final TagKey<EntityType<?>> ADDITIONAL_SHOOT_COOLDOWN_WHITELIST = TagKey.create(
            Registries.ENTITY_TYPE, MobsTacz.modLoc("additional_shoot_cooldown_whitelist"));
    public static final TagKey<EntityType<?>> ADDITIONAL_SHOOT_COOLDOWN_BLACKLIST = TagKey.create(
            Registries.ENTITY_TYPE, MobsTacz.modLoc("additional_shoot_cooldown_blacklist"));

    public static final TagKey<EntityType<?>> CONSUME_AMMO_WHITELIST = TagKey.create(
            Registries.ENTITY_TYPE, MobsTacz.modLoc("consume_ammo_whitelist"));
    public static final TagKey<EntityType<?>> CONSUME_AMMO_BLACKLIST = TagKey.create(
            Registries.ENTITY_TYPE, MobsTacz.modLoc("consume_ammo_blacklist"));

    private ModEntityTypeTags() {
    }
}
