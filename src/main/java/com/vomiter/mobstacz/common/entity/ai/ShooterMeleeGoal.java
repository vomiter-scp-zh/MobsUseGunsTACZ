package com.vomiter.mobstacz.common.entity.ai;

import com.vomiter.neurolib.common.entity.generic.MutatedMeleeGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class ShooterMeleeGoal extends MutatedMeleeGoal {
    private final IShootingGoal boundShootingGoal;
    public ShooterMeleeGoal(MeleeAttackGoal basicGoal, IShootingGoal shootingGoal) {
        super(basicGoal);
        boundShootingGoal = shootingGoal;
        setExtraUseCheck(goal -> !boundShootingGoal.shouldStayInShootingPose());
    }
}
