package com.vomiter.mobstacz;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MobsTacz.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue MOB_BULLET_DAMAGE_MULTIPLIER_SPEC;
    private static final ForgeConfigSpec.BooleanValue MOB_GLOWS_AFTER_SHOOTING_SPEC;

    private static final ForgeConfigSpec.BooleanValue ADDITIONAL_SHOOT_COOLDOWN_SPEC;
    private static final ForgeConfigSpec.BooleanValue CONSUME_AMMO_SPEC;

    public static double MOB_BULLET_DAMAGE_MULTIPLIER = 0.5D;
    public static boolean MOB_GLOWS_AFTER_SHOOTING = false;
    public static boolean ADDITIONAL_SHOOT_COOLDOWN = true;
    public static boolean CONSUME_AMMO = true;

    static {
        BUILDER.push("Mob Shooting");

        MOB_BULLET_DAMAGE_MULTIPLIER_SPEC = BUILDER
                .comment("Damage multiplier for bullets shot by mobs. Range: 0.0 ~ 1.0. Default: 0.5")
                .defineInRange("mobBulletDamageMultiplier", 0.5D, 0.0D, 1.0D);

        MOB_GLOWS_AFTER_SHOOTING_SPEC = BUILDER
                .comment("If true, mobs will gain the glowing effect after shooting.")
                .define("mobGlowsAfterShooting", false);

        ADDITIONAL_SHOOT_COOLDOWN_SPEC = BUILDER
                .comment("Default extra delay after a mob shoots. Individual overrides and entity type tags take priority.")
                .define("additional_shoot_cooldown", true);

        CONSUME_AMMO_SPEC = BUILDER
                .comment("Default ammo consumption for mobs when shooting and reloading. Individual overrides and entity type tags take priority.")
                .define("consume_ammo", true);

        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        MOB_BULLET_DAMAGE_MULTIPLIER = MOB_BULLET_DAMAGE_MULTIPLIER_SPEC.get();
        MOB_GLOWS_AFTER_SHOOTING = MOB_GLOWS_AFTER_SHOOTING_SPEC.get();
        ADDITIONAL_SHOOT_COOLDOWN = ADDITIONAL_SHOOT_COOLDOWN_SPEC.get();
        CONSUME_AMMO = CONSUME_AMMO_SPEC.get();
    }
}