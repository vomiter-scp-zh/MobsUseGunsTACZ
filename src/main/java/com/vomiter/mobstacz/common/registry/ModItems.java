package com.vomiter.mobstacz.common.registry;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public class ModItems {
    static DeferredRegister<Item> ITEMS
            = ModRegistries.createRegistry(Registries.ITEM);
}
