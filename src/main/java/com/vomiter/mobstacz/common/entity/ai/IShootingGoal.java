package com.vomiter.mobstacz.common.entity.ai;

import com.vomiter.mobstacz.MobsTacz;
import net.minecraft.world.entity.ai.goal.Goal;

public interface IShootingGoal {
    default boolean shouldStayInShootingPose(){
        if(this instanceof Goal goal) {
            MobsTacz.LOGGER.info("[MTACZ] shouldStayInShootingPose = {}", goal.canContinueToUse());
            return goal.canContinueToUse();
        }
        return false;
    }
}
