package com.vomiter.mobstacz.mixin;

import com.vomiter.mobstacz.common.entity.ai.GunMode;
import com.vomiter.mobstacz.common.entity.ai.IMobGunState;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityGunStateMixin implements IMobGunState {

    @Unique
    private GunMode mode;

    @Unique
    private float mtacz$aimYawOffset;

    @Unique
    private float mtacz$aimPitchOffset;


    @Unique
    public GunMode mtacz$getMode() {
        if ((Object) this instanceof Mob mob && mob.level().isClientSide()) {
            return GunMode.values()[mob.getEntityData().get(MTACZ_GUN_MODE)];
        }

        return mode;
    }

    @Unique
    public void mtacz$setMode(GunMode mode) {
        this.mode = mode;
        if ((Object) this instanceof Mob mob && !mob.level().isClientSide()) {
            mob.getEntityData().set(MTACZ_GUN_MODE, mode.ordinal());
        }
    }

    @Unique
    public float mtacz$getAimYawOffset() {
        if ((Object) this instanceof Mob mob && mob.level().isClientSide()) {
            return mob.getEntityData().get(MTACZ_AIM_YAW_OFFSET);
        }
        return mtacz$aimYawOffset;
    }

    @Unique
    public void mtacz$setAimYawOffset(float aimYawOffset) {
        this.mtacz$aimYawOffset = aimYawOffset;
        if ((Object) this instanceof Mob mob && !mob.level().isClientSide()) {
            mob.getEntityData().set(MTACZ_AIM_YAW_OFFSET, aimYawOffset);
        }
    }

    @Unique
    public float mtacz$getAimPitchOffset() {
        if ((Object) this instanceof Mob mob && mob.level().isClientSide()) {
            return mob.getEntityData().get(MTACZ_AIM_PITCH_OFFSET);
        }

        return mtacz$aimPitchOffset;
    }

    @Unique
    public void mtacz$setAimPitchOffset(float aimPitchOffset) {
        this.mtacz$aimPitchOffset = aimPitchOffset;
        if ((Object) this instanceof Mob mob && !mob.level().isClientSide()) {
            mob.getEntityData().set(MTACZ_AIM_PITCH_OFFSET, aimPitchOffset);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mtacz$defineData(EntityType p_20966_, Level p_20967_, CallbackInfo ci){
        if ((Object)this instanceof Mob mob){
            var entityData = mob.getEntityData();
            entityData.define(MTACZ_AIM_PITCH_OFFSET, 0.0F);
            entityData.define(MTACZ_AIM_YAW_OFFSET, 0.0F);
            entityData.define(MTACZ_GUN_MODE, GunMode.MELEE.ordinal());
        }
    }

    @Unique
    private static final EntityDataAccessor<Float> MTACZ_AIM_PITCH_OFFSET =
            SynchedEntityData.defineId(Mob.class, EntityDataSerializers.FLOAT);

    @Unique
    private static final EntityDataAccessor<Float> MTACZ_AIM_YAW_OFFSET =
            SynchedEntityData.defineId(Mob.class, EntityDataSerializers.FLOAT);

    @Unique
    private static final EntityDataAccessor<Integer> MTACZ_GUN_MODE =
            SynchedEntityData.defineId(Mob.class, EntityDataSerializers.INT);
}
