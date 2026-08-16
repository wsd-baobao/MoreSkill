package com.zjw.moreskill.skill.trading;

import com.zjw.moreskill.skill.AbstractSkill;
import net.minecraft.network.chat.Component;

/**
 * 交易技能
 */
public class Trading extends AbstractSkill {

    @Override
    public Component getName() {
        return Component.translatable("skill.moreskill.trading");
    }

    @Override
    public int getExpForNextLevel() {
        double value = 100 * Math.pow(2, getLevel());
        return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }
}