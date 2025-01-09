package com.zjw.moreskill.skill.combat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

/**
 * Represents a player's combat skills and experience.
 * Handles leveling up and experience points.
 */

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

public class Combat implements INBTSerializable<CompoundTag> {
    private int exp;
    private int level;
    private static final int MAX_LEVEL = 100;
    private static final int BASE_EXP = 100;
    private static final float EXPONENTIAL_SCALING = 1.2f;

    public Combat() {
        this(0, 0);
    }

    public Combat(int level, int exp) {
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
        if (nbt.contains("Level")) {
            setLevel(nbt.getInt("Level"));
        } else {
            // No level data found, setting default level to 0
            setLevel(0);
        }
        if (nbt.contains("Experience")) {
            setExp(nbt.getInt("Experience"));
        } else {
            // No experience data found, setting default experience to 0
            setExp(0);
        }
    }
    public Component getName() {
        return Component.translatable("skill.moreskill.combat");
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Adds combat experience points to the player.
     * @param player the player to add experience to
     * @param exp the amount of experience to add
     */
    public void addCombatExp(Player player, int exp) {
        this.exp += exp;
        
              int requiredExp = getRequiredExpForNextLevel();
        while (this.exp >= requiredExp && this.level < MAX_LEVEL) {
            this.level++;
            this.exp = 0;
            requiredExp = getRequiredExpForNextLevel();
        }
    }

    /**
     * Calculates the required experience points for the next level.
     * @return the required experience points
     */
    public int getRequiredExpForNextLevel() {
        // Calculates the required experience points using an exponential formula 
        return (int) (BASE_EXP * Math.pow(EXPONENTIAL_SCALING, this.level));
    }
}
