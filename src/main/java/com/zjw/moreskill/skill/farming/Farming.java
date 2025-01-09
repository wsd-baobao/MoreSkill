package com.zjw.moreskill.skill.farming;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.INBTSerializable;

public class Farming implements INBTSerializable<CompoundTag> {
    private int level;
    private int exp;
    public static final int MAX_LEVEL = 100;

    private static final int BASE_EXP = 100;
    private static final float LEVEL_SCALING = 0.08f;
    private static final float EXPONENTIAL_SCALING = 1.035f;

    public Farming(int level, int xp) {
        this.level = level;
        this.exp = xp;
    }

    public Farming() {
        this(0, 0);
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
            System.out.println("娌℃湁绛夌骇鏁版嵁");
            setLevel(0);
        }
        if (nbt.contains("Experience")) {
            setExp(nbt.getInt("Experience"));
        } else {
            System.out.println("娌℃湁缁忛獙鏁版嵁");
            setExp(0);
        }
    }

    public Component getName() {
        return Component.translatable("skill.moreskill.farming");
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


    public int getExpForLevel() {
        return (int) (BASE_EXP *
                (1 + level * LEVEL_SCALING) *
                Math.pow(EXPONENTIAL_SCALING, level));
    }

   
    public void addExp(int expGain) {
        if (level >= MAX_LEVEL) {
            return;
        }

        exp += expGain;

       
        while (level < MAX_LEVEL) {
        int requiredExp = getExpForLevel();
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

    

    
    public float getLevelProgress() {
        if (level >= MAX_LEVEL) {
            return 100;
        }
        return (exp * 100.0f) / getExpForLevel();
    }

    
    public int getExpToNextLevel() {
        if (level >= MAX_LEVEL) {
            return 0;
        }
        return getExpForLevel() - exp;
    }

   
    public int getTotalExpToNextLevel() {
        if (level >= MAX_LEVEL) {
            return 0;
        }
        return getExpForLevel();
    }
}
