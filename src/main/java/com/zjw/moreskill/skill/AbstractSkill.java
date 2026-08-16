package com.zjw.moreskill.skill;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 技能数据类公共基类：集中处理等级/经验字段、NBT 序列化、经验累积与自动升级逻辑。
 * 各技能子类只需实现 {@link #getName()} 与 {@link #getExpForNextLevel()} 定义各自名称与成长曲线，
 * 保留存档格式（NBT key 为 "Level"/"Experience"）与升级行为（保留溢出经验 + while 循环）不变。
 */
public abstract class AbstractSkill implements INBTSerializable<CompoundTag> {

    public static final int MAX_LEVEL = 100;

    private int level;
    private int exp;

    protected AbstractSkill() {
        this(0, 0);
    }

    protected AbstractSkill(int level, int exp) {
        this.level = level;
        this.exp = exp;
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
        setLevel(nbt.contains("Level") ? nbt.getInt("Level") : 0);
        setExp(nbt.contains("Experience") ? nbt.getInt("Experience") : 0);
    }

    public abstract Component getName();

    public abstract int getExpForNextLevel();

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
     * 增加经验值，达到升级条件时自动升级（保留溢出经验）。
     */
    public void addExp(int expGain) {
        if (level >= MAX_LEVEL) {
            return;
        }
        exp += expGain;
        while (level < MAX_LEVEL) {
            int requiredExp = getExpForNextLevel();
            if (exp < requiredExp) {
                break;
            }
            exp -= requiredExp;
            level++;
            if (level >= MAX_LEVEL) {
                exp = 0;
                break;
            }
        }
    }

    /**
     * 当前等级升级进度（百分比，满级返回 100）。
     */
    public float getLevelProgress() {
        if (level >= MAX_LEVEL) {
            return 100;
        }
        return (exp * 100.0f) / getExpForNextLevel();
    }

    /**
     * 升到下一级还需要的经验值（满级返回 0）。
     */
    public int getExpToNextLevel() {
        if (level >= MAX_LEVEL) {
            return 0;
        }
        return getExpForNextLevel() - exp;
    }
}