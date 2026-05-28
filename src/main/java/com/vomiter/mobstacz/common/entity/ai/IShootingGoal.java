package com.vomiter.mobstacz.common.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;

public interface IShootingGoal {
    default boolean shouldStayInShootingPose(){
        if(this instanceof Goal goal) return goal.canContinueToUse();
        return false;
    }
}
