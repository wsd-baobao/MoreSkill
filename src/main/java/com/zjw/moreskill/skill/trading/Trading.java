package com.zjw.moreskill.skill.trading;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 交易技能
 */

public class Trading implements INBTSerializable<CompoundTag> {

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
                setLevel(0); // 默认值
            }
            if (nbt.contains("Experience")) {
                setExp(nbt.getInt("Experience"));
            } else {
                setExp(0); // 默认值
            }
        }
    
        public Component getName() {
            return Component.translatable("skill.moreskill.trading");
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
    
        public void addExp(int expGain) {
            if (level >= MAX_LEVEL) {
                return;
            }
            this.exp += expGain;
            while (level < MAX_LEVEL) {
                int requiredExp = getExpForNextLevel();
                if (this.exp < requiredExp) {
                    break;
                }
                this.exp -= requiredExp;
                level++;
                if (level >= MAX_LEVEL) {
                    this.exp = 0;
                    break;
                }
            }
        }

    public int getExpForNextLevel() {
       double value = 100 * Math.pow(2, this.level);
       return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

}
