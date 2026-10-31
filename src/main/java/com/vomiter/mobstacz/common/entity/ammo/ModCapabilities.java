package com.vomiter.mobstacz.common.entity.ammo;

import com.vomiter.mobstacz.MobsTacz;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public final class ModCapabilities {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MobsTacz.MOD_ID);
    public static final Supplier<AttachmentType<MobAmmoHandler>> MOB_AMMO = ATTACHMENTS.register(
            "mob_ammo", () -> AttachmentType.serializable(() -> new MobAmmoHandler(4)).build());

    public static Optional<IMobAmmoHandler> getMobAmmo(Entity entity) {
        return entity instanceof Mob ? Optional.of(entity.getData(MOB_AMMO)) : Optional.empty();
    }
}
