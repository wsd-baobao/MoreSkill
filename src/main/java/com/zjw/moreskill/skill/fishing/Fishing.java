package com.zjw.moreskill.skill.fishing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Random;



/*
 * 钓鱼技能类
 */
public class Fishing implements INBTSerializable<CompoundTag> {
    private int level;
    private int exp;
    private static final int MAX_LEVEL = 100;

    public Fishing() {
        this.level = 0;
        this.exp = 0;
    }

    // 保存技能数据
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("Level", this.getLevel());
        compoundTag.putInt("Experience", this.getExp());
        return compoundTag;
    }

    // 加载技能数据
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Level")) {
            setLevel(nbt.getInt("Level"));
        } else {
            setLevel(0);
        }
        if (nbt.contains("Experience")) {
            setExp(nbt.getInt("Experience"));
        } else {
            setExp(0);
        }
    }

    
    public void addExp(Player player, int exp) {
        this.exp += exp;
        while (this.exp >= getExpForNextLevel() && this.level < MAX_LEVEL) {
            this.level++;
            this.exp = 0;
        }
        if (this.level >= MAX_LEVEL) {
            this.exp = 0;
        }
    }

    public Component getName() {
        return Component.translatable("skill.moreskill.fishing");
    }

    
    public int numberOfItemsToFish() {
        return new Random().nextInt(Math.min(11, (level / 10) + 2));
    }

    public int getExpForNextLevel() {
        return 100 + (this.level * 50);
    }

  
    public int getLevel() {
        return this.level;
    }


 
    public int getExp() {
        return this.exp;
    }

  

    
    public void setLevel(int level) {
        this.level = level;
    }

  
    public void setExp(int exp) {
        this.exp = exp;

    }
}

