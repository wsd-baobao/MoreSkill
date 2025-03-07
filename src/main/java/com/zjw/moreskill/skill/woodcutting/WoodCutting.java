package com.zjw.moreskill.skill.woodcutting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 伐木技能
 */

public class WoodCutting implements INBTSerializable<CompoundTag> {

    private static final int MAX_LEVEL = 100;
    private int level;
    private int exp;

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
        return Component.translatable("skill.moreskill.woodcutting");
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


    public void addExp(int exp) {

        this.exp += exp;
        if (this.exp >= getExpForLevel()) {
            this.exp = 0;
            int newLevel = this.level + 1;
            if (newLevel <= MAX_LEVEL) {
                this.level = newLevel;
            }
        }
    }

    public int getExpForLevel() {
        return (int) (100 * Math.pow(1.1, this.level));
    }
}
