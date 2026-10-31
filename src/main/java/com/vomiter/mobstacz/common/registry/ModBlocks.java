package com.vomiter.mobstacz.common.registry;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS
            = ModRegistries.createRegistry(Registries.BLOCK);
}
