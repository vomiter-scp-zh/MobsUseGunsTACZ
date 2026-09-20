package com.vomiter.mobstacz.common.event;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.vomiter.mobstacz.common.entity.MobGunUtils;
import com.vomiter.mobstacz.common.entity.ai.*;
import com.vomiter.neurolib.common.entity.generic.GoalMutateUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;

import java.util.ArrayList;
import java.util.Objects;

public class EquipGunEvent {
    static void onMobEquipGun(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }

        if (event.getSlot() != EquipmentSlot.MAINHAND) {
            return;
        }

        ItemStack fromStack = event.getFrom();
        ItemStack toStack = event.getTo();

        IGun fromGun = IGun.getIGunOrNull(fromStack);
        IGun toGun = IGun.getIGunOrNull(toStack);

        boolean hadGun = fromGun != null;
        boolean hasGun = toGun != null;

        // 非槍 → 非槍，與 MobTACZ 無關。
        if (!hadGun && !hasGun) {
            return;
        }

        IMobGunState state = (IMobGunState) mob;
        IGunOperator operator = IGunOperator.fromLivingEntity(mob);

        // 非槍 → 槍
        if (!hadGun) {
            ensureGunGoalsInjected(mob);

            MobGunUtils.giveInitialAmmoStack(mob, toStack);

            operator.draw(mob::getMainHandItem);

            state.mtacz$setAimPitchOffset(0);
            state.mtacz$setAimYawOffset(0);
            state.mtacz$setMode(GunMode.RANGED);
            return;
        }

        // 槍 → 非槍
        if (!hasGun) {
            operator.initialData();

            state.mtacz$setAimPitchOffset(0);
            state.mtacz$setAimYawOffset(0);
            state.mtacz$setMode(GunMode.MELEE);
            return;
        }

        // 槍 → 槍。
        ResourceLocation fromGunId = fromGun.getGunId(fromStack);
        ResourceLocation toGunId = toGun.getGunId(toStack);

        if (Objects.equals(fromGunId, toGunId)) {
            return;
        }

        // 確定換成不同 gun ID。
        ensureGunGoalsInjected(mob);

        operator.draw(mob::getMainHandItem);

        state.mtacz$setAimPitchOffset(0);
        state.mtacz$setAimYawOffset(0);
        state.mtacz$setMode(GunMode.RANGED);
    }

    private static final int DEFAULT_GUN_GOAL_PRIORITY = 4;

    private static void ensureGunGoalsInjected(Mob mob) {
        boolean alreadyInjected = mob.goalSelector
                .getAvailableGoals()
                .stream()
                .anyMatch(wrapped -> wrapped.getGoal() instanceof CrazyShooterShootingGoal
                );

        if (alreadyInjected) {
            return;
        }

        CrazyShooterShootingGoal shootingGoal =
                new CrazyShooterShootingGoal(
                        mob,
                        0.5D,
                        8.0F,
                        16,
                        32,
                        200,
                        4.0F
                );

        CrazyShooterReloadGoal reloadGoal = new CrazyShooterReloadGoal(mob);

        CrazyShooterAimGoal aimGoal = new CrazyShooterAimGoal(mob, 0.01F, 8.0F);

        ArrayList<GoalMutateUtils.Replacement> replacements = new ArrayList<>();

        int firstMeleePriority = GoalMutateUtils.replaceAllMeleeWithMutated(
                        mob.goalSelector,
                        melee -> new ShooterMeleeGoal(melee, shootingGoal),
                        replacements
                );

        int gunGoalPriority = replacements.isEmpty() ? DEFAULT_GUN_GOAL_PRIORITY : firstMeleePriority;

        mob.goalSelector.addGoal(gunGoalPriority, shootingGoal);
        mob.goalSelector.addGoal(gunGoalPriority, reloadGoal);
        mob.goalSelector.addGoal(gunGoalPriority, aimGoal);

        if (mob instanceof PathfinderMob pathfinderMob) {
            mob.goalSelector.addGoal(
                    gunGoalPriority,
                    new ShooterGetAmmoGoal(pathfinderMob, 1.0D, 100, 16.0D)
            );
        }
    }
}
