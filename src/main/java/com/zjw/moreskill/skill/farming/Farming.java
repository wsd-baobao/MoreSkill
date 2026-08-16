package com.zjw.moreskill.skill.farming;

import com.zjw.moreskill.skill.AbstractSkill;
import net.minecraft.network.chat.Component;

public class Farming extends AbstractSkill {
    private static final int BASE_EXP = 100;
    private static final float LEVEL_SCALING = 0.08f;
    private static final float EXPONENTIAL_SCALING = 1.035f;

    public Farming() {
    }

    public Farming(int level, int xp) {
        super(level, xp);
    }

    @Override
    public Component getName() {
        return Component.translatable("skill.moreskill.farming");
    }

    @Override
    public int getExpForNextLevel() {
        int level = getLevel();
        return (int) (BASE_EXP *
                (1 + level * LEVEL_SCALING) *
                Math.pow(EXPONENTIAL_SCALING, level));
    }

    /**
     * 升级总共需要的经验值
     */
    public int getTotalExpToNextLevel() {
        if (getLevel() >= MAX_LEVEL) {
            return 0;
        }
        return getExpForNextLevel();
    }
}