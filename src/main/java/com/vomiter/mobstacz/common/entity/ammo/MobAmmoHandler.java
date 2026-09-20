package com.vomiter.mobstacz.common.entity.ammo;

import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

/**
 * 只接受 TACZ 彈藥與彈藥箱的 inventory。
 */
public class MobAmmoHandler
        extends ItemStackHandler
        implements IMobAmmoHandler {

    public MobAmmoHandler(int slots) {
        super(slots);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getItem() instanceof IAmmo
                || stack.getItem() instanceof IAmmoBox;
    }
}