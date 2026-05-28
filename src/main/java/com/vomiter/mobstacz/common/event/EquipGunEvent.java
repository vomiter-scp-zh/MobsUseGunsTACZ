package com.vomiter.mobstacz.common.event;

import com.tacz.guns.init.ModItems;
import com.vomiter.mobstacz.MobsTacz;
import com.vomiter.mobstacz.common.entity.IAmmoStorage;
import com.vomiter.mobstacz.common.entity.ai.CrazyShooterShootingGoal;
import com.vomiter.mobstacz.common.entity.ai.IShootingGoal;
import com.vomiter.mobstacz.common.entity.ai.ShooterMeleeGoal;
import com.vomiter.neurolib.common.entity.generic.GoalMutateUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;

import java.util.ArrayList;

public class EquipGunEvent {
    static void onMobEquipGun(LivingEquipmentChangeEvent event){
        LivingEntity entity = event.getEntity();
        if(!(entity instanceof Mob mob)) return;
        if(!event.getSlot().equals(EquipmentSlot.MAINHAND)) return;
        if(!event.getTo().is(ModItems.MODERN_KINETIC_GUN.get())) return;
        boolean isInjected = mob.goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.getGoal() instanceof IShootingGoal);
        if(isInjected) return;
        var shootingGoal = new CrazyShooterShootingGoal(mob, 0.5, 8, 16, 32, 200);
        int firstPriority = GoalMutateUtils.replaceAllMeleeWithMutated(
                mob.goalSelector,
                (o) -> new ShooterMeleeGoal(o, shootingGoal),
                new ArrayList<>()
        );
        mob.goalSelector.addGoal(firstPriority, shootingGoal);
        if(mob instanceof IAmmoStorage storage && !storage.mobstacz$hasBasicAmmo()){
            storage.mobstacz$setAmmoCount(2);
            storage.mobstacz$setHasBasicAmmo(true);
            MobsTacz.LOGGER.info("[MTACZ] basic ammo count = {}", storage.mobstacz$getAmmoCount());
        }
    }
}
