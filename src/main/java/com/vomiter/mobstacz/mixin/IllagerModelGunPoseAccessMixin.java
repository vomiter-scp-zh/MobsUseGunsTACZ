package com.vomiter.mobstacz.mixin;

import com.vomiter.mobstacz.client.animation.IGunPoseModelAccess;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(IllagerModel.class)
public abstract class IllagerModelGunPoseAccessMixin implements IGunPoseModelAccess {
    @Shadow @Final private ModelPart head;
    @Shadow @Final private ModelPart rightArm;
    @Shadow @Final private ModelPart leftArm;

    // IllagerModel 原版沒有 body 欄位，如果你需要 body rotation，
    // 建議另外 shadow root，或在這裡直接回傳 root / dummy。
    @Shadow @Final private ModelPart root;

    @Override
    public ModelPart mtacz$getHead() {
        return head;
    }

    @Override
    public ModelPart mtacz$getBody() {
        return root;
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