package com.zjw.moreskill.skill.woodcutting;

import com.zjw.moreskill.skill.AbstractSkill;
import net.minecraft.network.chat.Component;

/**
 * 伐木技能
 */
public class WoodCutting extends AbstractSkill {

    @Override
    public Component getName() {
        return Component.translatable("skill.moreskill.woodcutting");
    }

    @Override
    public int getExpForNextLevel() {
        return (int) (100 * Math.pow(1.1, getLevel()));
    }
}