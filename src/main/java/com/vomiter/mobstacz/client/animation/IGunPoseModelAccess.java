package com.vomiter.mobstacz.client.animation;

import net.minecraft.client.model.geom.ModelPart;

public interface IGunPoseModelAccess {
    ModelPart mtacz$getHead();
    ModelPart mtacz$getBody();
    ModelPart mtacz$getRightArm();
    ModelPart mtacz$getLeftArm();

    default boolean mtacz$hasBody() {
        return mtacz$getBody() != null;
    }

    default void mtacz$setArmsVisible(boolean visible) {
        mtacz$getRightArm().visible = visible;
        mtacz$getLeftArm().visible = visible;
    }
}