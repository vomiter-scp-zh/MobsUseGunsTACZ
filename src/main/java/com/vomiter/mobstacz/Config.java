package com.vomiter.mobstacz;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = MobsTacz.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.DoubleValue MOB_BULLET_DAMAGE_MULTIPLIER_SPEC;
    private static final ModConfigSpec.BooleanValue MOB_GLOWS_AFTER_SHOOTING_SPEC;

    public static double MOB_BULLET_DAMAGE_MULTIPLIER = 0.5D;
    public static boolean MOB_GLOWS_AFTER_SHOOTING = false;

    static {
        BUILDER.push("Mob Shooting");

        MOB_BULLET_DAMAGE_MULTIPLIER_SPEC = BUILDER
                .comment("Damage multiplier for bullets shot by mobs. Range: 0.0 ~ 1.0. Default: 0.5")
                .defineInRange("mobBulletDamageMultiplier", 0.5D, 0.0D, 1.0D);

        MOB_GLOWS_AFTER_SHOOTING_SPEC = BUILDER
                .comment("If true, mobs will gain the glowing effect after shooting.")
                .define("mobGlowsAfterShooting", false);

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        MOB_BULLET_DAMAGE_MULTIPLIER = MOB_BULLET_DAMAGE_MULTIPLIER_SPEC.get();
        MOB_GLOWS_AFTER_SHOOTING = MOB_GLOWS_AFTER_SHOOTING_SPEC.get();
    }
}