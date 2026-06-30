package com.vomiter.mobstacz.common.entity.ai;

import com.vomiter.neurolib.common.entity.generic.MutatedMeleeGoal;
import com.vomiter.neurolib.mixin.MeleeAttackGoalAccessor;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class ShooterMeleeGoal extends MutatedMeleeGoal {
    private final IShootingGoal boundShootingGoal;
    public ShooterMeleeGoal(MeleeAttackGoal basicGoal, IShootingGoal shootingGoal) {
        super(basicGoal);
        boundShootingGoal = shootingGoal;

        setExtraUseCheck(goal -> {
            if(goal instanceof MeleeAttackGoalAccessor acc){
                if(acc.getMob() instanceof IMobGunState gunState){
                    return gunState.mtacz$getMode() == GunMode.MELEE;
                }
            }
            return true;
        });
    }
}
