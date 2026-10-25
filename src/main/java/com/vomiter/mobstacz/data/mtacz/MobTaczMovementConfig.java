package com.vomiter.mobstacz.data.mtacz;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public record MobTaczMovementConfig(double strafeAmplitude, float strafeSpeed) {
    public static final MobTaczMovementConfig DEFAULT = new MobTaczMovementConfig(2.0D, 0.5F);

    public MobTaczMovementConfig {
        if (!Double.isFinite(strafeAmplitude) || strafeAmplitude < 0 || strafeAmplitude > 32) {
            throw new IllegalArgumentException("strafe_amplitude must be finite and within [0, 32] blocks");
        }
        if (!Float.isFinite(strafeSpeed) || strafeSpeed < 0 || strafeSpeed > 1) {
            throw new IllegalArgumentException("strafe_speed must be finite and within [0, 1]");
        }
    }

    public static MobTaczMovementConfig parse(JsonObject obj) {
        return new MobTaczMovementConfig(
                GsonHelper.getAsDouble(obj, "strafe_amplitude", DEFAULT.strafeAmplitude()),
                GsonHelper.getAsFloat(obj, "strafe_speed", DEFAULT.strafeSpeed())
        );
    }
}
