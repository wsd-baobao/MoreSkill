package com.zjw.moreskill.skill.fishing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IFishing extends INBTSerializable<CompoundTag>{
    int getLevel();

    void setLevel(int level);

    int getExpToNextLevel();

    int getExp();

    void setExp(int exp);

    void addExp(Player player, int exp);

    int numberOfItemsToFish();
}
