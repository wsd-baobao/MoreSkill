package com.zjw.moreskill.skill.cooking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 鐑归オ鎶€鑳? * 鍔犲揩鐑归オ閫熷害 涓嶄細
 * 鎻愰珮鎭㈠閲? 鍙樼浉瀹炵幇
 * 鐗规畩椋熻氨(鏂扮墿鍝?  鎼佺疆
 * 椋熷搧鍔燽uff      鎼佺疆
 */

public class Cooking implements INBTSerializable<CompoundTag> {
    private int level;
    private int exp;
    public static final int MAX_LEVEL = 100;

    // 娣诲姞甯搁噺
    private static final int BASE_EXP = 100;
    private static final float EXPONENTIAL_SCALING = 1.2f;

    public Cooking(int level, int xp) {
        this.level = level;
        this.exp = xp;
    }

    public Cooking() {
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
        return Component.translatable("skill.moreskill.cooking");
    }
    public float getCookingSpeedMultiplier() {
        
        return 1f + (float) (Math.log(100 + 1) / Math.log(MAX_LEVEL) * 1f);
    }

    /**
     * 鑾峰彇褰撳墠绛夌骇
     * Get current level
     * @return 褰撳墠鐑归オ鎶€鑳界瓑绾?     */
    public int getLevel() {
        return this.level;
    }

    /**
     * 璁剧疆绛夌骇
     * Set level
     * @param level 鏂扮殑绛夌骇
     */
    public void setLevel(int level) {
        this.level = Math.min(Math.max(level, 0), MAX_LEVEL);
    }

    /**
     * 鑾峰彇褰撳墠缁忛獙
     * Get current experience
     * @return 褰撳墠缁忛獙鍊?     */
    public int getExp() {
        return this.exp;
    }

    /**
     * 璁剧疆缁忛獙
     * Set experience
     * @param exp 鏂扮殑缁忛獙鍊?     */
    public void setExp(int exp) {
        this.exp = Math.max(exp, 0);
    }

    /**
     * 璁＄畻棰濆钀ュ吇
     * Calculate extra nutrition
     * @return 棰濆钀ュ吇鍊?     */
    public float calculateExtraNutrition() {
        // 姣?绾у鍔?.5鐐归澶栭ケ椋熷害
        return this.level / 10f;
    }

    /**
     * 璁＄畻棰濆楗卞拰搴?     * Calculate extra saturation
     * @return 棰濆楗卞拰搴﹀€?     */
    public float calculateExtraSaturation() {
        // 姣?0绾у鍔?.1鐐归澶栭ケ鍜屽害
        return this.level / 100f;
    }

    /**
     * 娣诲姞鐑归オ缁忛獙
     * @param player 鐜╁
     * @param exp 瑕佹坊鍔犵殑缁忛獙鍊?     */
    public void addCookingExp(Player player, int exp) {
        this.exp += exp;
        
               int requiredExp = getRequiredExpForNextLevel();
        while (this.exp >= requiredExp && this.level < MAX_LEVEL) {
            this.level++;
            this.exp = 0;
            requiredExp = getRequiredExpForNextLevel();
           
        }
    }

    /**
     * 璁＄畻鍗囩骇鎵€闇€缁忛獙
     * @return 鍗囩骇鎵€闇€缁忛獙鍊?     */
    public int getRequiredExpForNextLevel() {
        // 浣跨敤鎸囨暟澧為暱鍏紡
        return (int) (BASE_EXP * Math.pow(EXPONENTIAL_SCALING, this.level));
    }
}
