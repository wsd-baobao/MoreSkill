package com.zjw.moreskill.skill.smithing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 锻造技能
 */

public class Smithing implements INBTSerializable<CompoundTag> {
    private int level;
    private int exp;
    public static final int MAX_LEVEL = 100;

    // 基础经验值
    private static final int BASE_EXP = 100;  // 提高基础经验
    private static final float LEVEL_SCALING = 0.08f; // 提高线性增长到8%
    private static final float EXPONENTIAL_SCALING = 1.035f; // 提高指数增长到3.5%

    public Smithing(int level, int xp) {
        this.level = level;
        this.exp = xp;
    }

    public Smithing() {

    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("Level", this.level);
        compoundTag.putInt("Experience", this.exp);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Level")) {
            setLevel(nbt.getInt("Level"));
        } else {
            System.out.println("没有等级数据");
            setLevel(0); // 默认值
        }
        if (nbt.contains("Experience")) {
            setExp(nbt.getInt("Experience"));
        } else {
            System.out.println("没有经验数据");
            setExp(0); // 默认值
        }
    }
    public Component getName() {
        return Component.translatable("skill.moreskill.smithing");
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    /**
     * 计算指定等级升级所需的经验值
     * 使用复合增长公式：基础经验 * (1 + 等级 * 线性增长) * (指数增长 ^ 等级)
     * @param level 当前等级
     * @return 升级所需经验
     */
    public int getExpForLevel() {
        return (int)(BASE_EXP * 
                    (1 + this.level * LEVEL_SCALING) * 
                    Math.pow(EXPONENTIAL_SCALING, this.level));
    }

    /**
     * 增加经验值，如果达到升级条件则自动升级
     * @param expGain 获得的经验值
     */
    public void addExp(int expGain) {
        if (level >= MAX_LEVEL) {
            return;
        }
        
        exp += expGain;
        
        // 检查是否可以升级
        while (level < MAX_LEVEL) {
            int requiredExp = getExpForLevel();
            if (exp < requiredExp) {
                break;
            }
            
            exp -= requiredExp;
            level++;
            
            // 如果达到最大等级，清空剩余经验
            if (level >= MAX_LEVEL) {
                exp = 0;
                break;
            }
        }
    }

    /**
     * 获取当前等级升级进度（百分比）
     * @return 升级进度（0-100）
     */
    public float getLevelProgress() {
        if (level >= MAX_LEVEL) {
            return 100;
        }
        return (exp * 100.0f) / getExpForLevel();
    }

    /**
     * 获取当前等级升级还需要的经验值
     * @return 还需经验值
     */
    public int getExpToNextLevel() {
        if (level >= MAX_LEVEL) {
            return 0;
        }
        return getExpForLevel() - exp;
    }
}
