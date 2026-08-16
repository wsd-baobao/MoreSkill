package com.zjw.moreskill.skill.cooking;

import com.zjw.moreskill.skill.AbstractSkill;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * 烹饪技能
 * 加快烹饪速度 不会
 * 提高恢复量  变相实现
 * 特殊食谱(新物品)  搁置
 * 食品加buff      搁置
 */
public class Cooking extends AbstractSkill {
    private static final int BASE_EXP = 100;
    private static final float EXPONENTIAL_SCALING = 1.2f;

    public Cooking() {
    }

    public Cooking(int level, int xp) {
        super(level, xp);
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(Math.min(Math.max(level, 0), MAX_LEVEL));
    }

    @Override
    public void setExp(int exp) {
        super.setExp(Math.max(exp, 0));
    }

    @Override
    public Component getName() {
        return Component.translatable("skill.moreskill.cooking");
    }

    public float getCookingSpeedMultiplier() {
        return 1f + (float) (Math.log(100 + 1) / Math.log(MAX_LEVEL) * 1f);
    }

    /**
     * 计算额外营养
     * @return 额外营养值
     */
    public float calculateExtraNutrition() {
        return getLevel() / 10f;
    }

    /**
     * 计算额外饱和度
     * @return 额外饱和度值
     */
    public float calculateExtraSaturation() {
        return getLevel() / 100f;
    }

    /**
     * 增加烹饪经验
     * @param player 玩家
     * @param exp 要增加的经验值
     */
    public void addCookingExp(Player player, int exp) {
        addExp(exp);
    }

    @Override
    public int getExpForNextLevel() {
        return (int) (BASE_EXP * Math.pow(EXPONENTIAL_SCALING, getLevel()));
    }
}