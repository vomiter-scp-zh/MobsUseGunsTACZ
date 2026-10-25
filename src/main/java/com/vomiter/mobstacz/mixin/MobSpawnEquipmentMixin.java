package com.vomiter.mobstacz.mixin;

import com.vomiter.mobstacz.common.entity.spawn.SpawnGunEquipment;
import com.vomiter.mobstacz.common.entity.spawn.SpawnGunEquipmentAccess;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobSpawnEquipmentMixin implements SpawnGunEquipmentAccess {
    @Unique
    private final SpawnGunEquipment mtacz$spawnGunEquipment = new SpawnGunEquipment();

    @Override
    public SpawnGunEquipment mtacz$getSpawnGunEquipment() {
        return mtacz$spawnGunEquipment;
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
    private void mtacz$protectSelectedSpawnGun(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if (mtacz$spawnGunEquipment != null
                && mtacz$spawnGunEquipment.blocksReplacement((Mob) (Object) this, slot, stack)) ci.cancel();
    }
}
