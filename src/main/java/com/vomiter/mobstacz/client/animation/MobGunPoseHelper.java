package com.vomiter.mobstacz.client.animation;

import com.tacz.guns.api.entity.ReloadState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public final class MobGunPoseHelper {
    private MobGunPoseHelper() {
    }

    public static void applyAimingPose(IGunPoseModelAccess model) {
        float headX = model.mtacz$getHead().xRot;

        model.mtacz$getBody().yRot = -0.5F;

        model.mtacz$getRightArm().xRot = -1.5F + headX;
        model.mtacz$getRightArm().yRot = 0F;
        model.mtacz$getRightArm().zRot = 0.05F;
        model.mtacz$getRightArm().z = -3;

        model.mtacz$getLeftArm().xRot = -1.4F + headX;
        model.mtacz$getLeftArm().yRot = 1F;
        model.mtacz$getLeftArm().zRot = -0.12F;
    }

    public static void applyReloadPose(
            IGunPoseModelAccess model,
            ReloadState.StateType type,
            float progress
    ) {
        float headX = model.mtacz$getHead().xRot;
        float headY = model.mtacz$getHead().yRot;

        ModelPart body = model.mtacz$getBody();
        if (body != null) {
            body.yRot = headY * 0.25F;
        }

        model.mtacz$getRightArm().xRot = -1.15F + headX;
        model.mtacz$getRightArm().yRot = -0.20F;
        model.mtacz$getRightArm().zRot = 0.05F;

        if (type == ReloadState.StateType.EMPTY_RELOAD_FEEDING
                || type == ReloadState.StateType.TACTICAL_RELOAD_FEEDING) {
            applyLeftArmReloadCycle(model, progress, headX);
        } else {
            model.mtacz$getLeftArm().xRot = -1.00F + headX * 0.35F;
            model.mtacz$getLeftArm().yRot = 0.18F;
            model.mtacz$getLeftArm().zRot = -0.12F;

            model.mtacz$getRightArm().zRot = 0.00F;
        }
    }

    private static void applyLeftArmReloadCycle(
            IGunPoseModelAccess model,
            float progress,
            float headX
    ) {
        float xRot;
        float yRot;
        float zRot;

        if (progress < 0.25F) {
            float t = progress / 0.25F;
            xRot = Mth.lerp(t, -0.95F, -1.15F);
            yRot = Mth.lerp(t,  0.52F,  0.18F);
            zRot = Mth.lerp(t,  0.00F, -0.28F);
        } else if (progress < 0.50F) {
            float t = (progress - 0.25F) / 0.25F;
            xRot = Mth.lerp(t, -1.15F, -0.80F);
            yRot = Mth.lerp(t,  0.18F,  0.72F);
            zRot = Mth.lerp(t, -0.28F,  0.10F);
        } else if (progress < 0.75F) {
            float t = (progress - 0.50F) / 0.25F;
            xRot = Mth.lerp(t, -0.80F, -1.10F);
            yRot = Mth.lerp(t,  0.72F,  0.22F);
            zRot = Mth.lerp(t,  0.10F, -0.22F);
        } else {
            float t = (progress - 0.75F) / 0.25F;
            xRot = Mth.lerp(t, -1.10F, -0.95F);
            yRot = Mth.lerp(t,  0.22F,  0.52F);
            zRot = Mth.lerp(t, -0.22F,  0.00F);
        }

        model.mtacz$getLeftArm().xRot = xRot + headX * 0.10F;
        model.mtacz$getLeftArm().yRot = yRot;
        model.mtacz$getLeftArm().zRot = zRot;
    }

    public static float getBoltProgress(float ageInTicks) {
        float local = ageInTicks % 6.0F; return local / 6.0F;
    }

    public static <T extends LivingEntity> void applyBoltPose(IGunPoseModelAccess model, float progress ) {
        float headX = model.mtacz$getHead().xRot;
        float headY = model.mtacz$getHead().yRot;
        float tri = progress <= 0.5f ? (progress * 2f) : (2f - progress * 2f);
        model.mtacz$getBody().yRot = headY * 0.4f;
        model.mtacz$getRightArm().yRot = Mth.lerp(tri, -0.30F + headY, -0.55F + headY);
        model.mtacz$getRightArm().xRot = Mth.lerp(tri, -1.35F + headX, -0.85F + headX);
        model.mtacz$getRightArm().zRot = Mth.lerp(tri, 0.00F, 0.25F);
        model.mtacz$getLeftArm().yRot = 0.75F + headY;
        model.mtacz$getLeftArm().xRot = -1.30F + headX;
        model.mtacz$getLeftArm().zRot = -0.10F;
    }
}