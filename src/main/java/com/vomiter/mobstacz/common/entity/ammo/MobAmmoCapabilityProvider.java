package com.vomiter.mobstacz.common.entity.ammo;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MobAmmoCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

    private final MobAmmoHandler handler;
    private final LazyOptional<IMobAmmoHandler> optional;

    public MobAmmoCapabilityProvider(int slots) {
        this.handler = new MobAmmoHandler(slots);
        this.optional = LazyOptional.of(() -> handler);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(
            @NotNull Capability<T> capability,
            @Nullable Direction side
    ) {
        return ModCapabilities.MOB_AMMO.orEmpty(
                capability,
                optional
        );
    }

    @Override
    public CompoundTag serializeNBT() {
        return handler.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        handler.deserializeNBT(tag);
    }

    public void invalidate() {
        optional.invalidate();
    }
}