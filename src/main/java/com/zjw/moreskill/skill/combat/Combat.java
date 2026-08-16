package com.zjw.moreskill.skill.combat;

import com.zjw.moreskill.skill.AbstractSkill;
import net.minecraft.network.chat.Component;

public class Combat extends AbstractSkill {
    private static final int BASE_EXP = 100;
    private static final float EXPONENTIAL_SCALING = 1.2f;

    public Combat() {
    }

    public Combat(int level, int exp) {
        super(level, exp);
    }

    @Override
    public Component getName() {
        return Component.translatable("skill.moreskill.combat");
    }

    /**
     * Adds combat experience points to the player.
     *
     * @param exp the amount of experience to add
     */
    public void addCombatExp(int expGain) {
        addExp(expGain);
    }

    /**
     * Calculates the required experience points for the next level.
     *
     * @return the required experience points
     */
    @Override
    public int getExpForNextLevel() {
        return (int) (BASE_EXP * Math.pow(EXPONENTIAL_SCALING, getLevel()));
    }
}