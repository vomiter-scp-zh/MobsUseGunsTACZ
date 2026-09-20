package com.vomiter.mobstacz.common.entity.ammo;

import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * MobTACZ 專用的 mob 備用彈藥 inventory。
 * 使用獨立介面是為了讓 Forge 建立不同於
 * ForgeCapabilities.ITEM_HANDLER 的 capability。
 */
public interface IMobAmmoHandler extends IItemHandlerModifiable {
}