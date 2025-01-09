package com.zjw.moreskill.skill.fishing;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Random;

import javax.print.attribute.standard.Compression;



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
        System.out.println("保存技能数据");
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("Level", this.getLevel());
        compoundTag.putInt("Experience", this.getExp());
        return compoundTag;
    }

    // 加载技能数据
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        System.out.println("加载技能数据");
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

    
    public void addExp(Player player, int exp) {
        this.exp += exp;
        int requiredExp = getRequiredExpForNextLevel();
        boolean leveledUp = false;
        while (this.exp >= requiredExp && this.level < MAX_LEVEL) {
            this.level++;
            this.exp = 0;
            leveledUp = true;
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

    public int getRequiredExpForNextLevel() {
        return 100 + (this.level * 50);
    }

  
    public int getLevel() {
        return this.level;
    }


 
    public int getExp() {
        return this.exp;
    }

    //返回还差多少经验升级
  
    public int getExpToNextLevel() {
        return getRequiredExpForNextLevel();
    }

    
    public void setLevel(int level) {
        this.level = level;
    }

  
    public void setExp(int exp) {
        this.exp = exp;

    }
}

