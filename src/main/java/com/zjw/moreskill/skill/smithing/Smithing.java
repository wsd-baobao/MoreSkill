package com.zjw.moreskill.skill.smithing;


import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 锻造技能
 */

public class Smithing implements INBTSerializable<CompoundTag> {
    private int level;
    private int exp;

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


}
