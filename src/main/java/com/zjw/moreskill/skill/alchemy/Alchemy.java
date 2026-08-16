package com.zjw.moreskill.skill.alchemy;

import com.zjw.moreskill.skill.AbstractSkill;
import net.minecraft.network.chat.Component;

/**
 * 炼金技能
 * 目前只有延长药水时间功能
 */
public class Alchemy extends AbstractSkill {

    @Override
    public Component getName() {
        return Component.translatable("skill.moreskill.alchemy");
    }

    @Override
    public int getExpForNextLevel() {
        return (int) (100 * Math.pow(1.1, getLevel()));
    }
}