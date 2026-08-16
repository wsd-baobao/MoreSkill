package com.zjw.moreskill.skill.smithing;

import com.zjw.moreskill.skill.AbstractSkill;
import net.minecraft.network.chat.Component;

/**
 * 锻造技能
 */
public class Smithing extends AbstractSkill {
    // 基础经验值
    private static final int BASE_EXP = 100;  // 提高基础经验
    private static final float LEVEL_SCALING = 0.08f; // 提高线性增长到8%
    private static final float EXPONENTIAL_SCALING = 1.035f; // 提高指数增长到3.5%

    public Smithing() {
    }

    public Smithing(int level, int xp) {
        super(level, xp);
    }

    @Override
    public Component getName() {
        return Component.translatable("skill.moreskill.smithing");
    }

    /**
     * 计算指定等级升级所需的经验值
     * 使用复合增长公式：基础经验 * (1 + 等级 * 线性增长) * (指数增长 ^ 等级)
     * @return 升级所需经验
     */
    @Override
    public int getExpForNextLevel() {
        int level = getLevel();
        return (int) (BASE_EXP *
                (1 + level * LEVEL_SCALING) *
                Math.pow(EXPONENTIAL_SCALING, level));
    }
}