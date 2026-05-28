package com.vomiter.mobstacz.mixin;

import com.vomiter.mobstacz.client.animation.IGunPoseModelAccess;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(HumanoidModel.class)
public abstract class HumanoidModelGunPoseAccessMixin implements IGunPoseModelAccess {
    @Final
    @Shadow public ModelPart head;
    @Final
    @Shadow public ModelPart body;
    @Final
    @Shadow public ModelPart rightArm;
    @Final
    @Shadow
    public ModelPart leftArm;

    @Override
    public ModelPart mtacz$getHead() {
        return head;
    }

    @Override
    public ModelPart mtacz$getBody() {
        return body;
    }

    @Override
    public ModelPart mtacz$getRightArm() {
        return rightArm;
    }

    @Override
    public ModelPart mtacz$getLeftArm() {
        return leftArm;
    }
}