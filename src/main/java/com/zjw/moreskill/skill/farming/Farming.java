package com.zjw.moreskill.skill.farming;

/**
 * 绉嶆鎶€鑳? * 鑳介噺鍚告敹
 * 鏍规嵁绉嶆绛夌骇姒傜巼鍑虹幇绋€鏈変綔鐗? */

import net.minecraft.nbt.CompoundTag;
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
     * 璁＄畻鎸囧畾绛夌骇鍗囩骇鎵€闇€鐨勭粡楠屽€? * 浣跨敤澶嶅悎澧為暱鍏紡锛氬熀纭€缁忛獙 * (1 + 绛夌骇 * 绾挎€у闀? * (鎸囨暟澧為暱
     * ^ 绛夌骇)
     * 
     * @param level 褰撳墠绛夌骇
     * @return 鍗囩骇鎵€闇€缁忛獙
     */
    public int getExpForLevel(int level) {
        return (int) (BASE_EXP *
                (1 + level * LEVEL_SCALING) *
                Math.pow(EXPONENTIAL_SCALING, level));
    }

   
    public void addExp(int expGain) {
        if (level >= MAX_LEVEL) {
            return;
        }

        exp += expGain;

        // 妫€鏌ユ槸鍚﹀彲浠ュ崌绾? 
        while (level < MAX_LEVEL) {
        int requiredExp = getExpForLevel(level);
        if (exp < requiredExp) {
            break;
        }

        exp -= requiredExp;
        level++;

        // 濡傛灉杈惧埌鏈€澶х瓑绾э紝娓呯┖鍓╀綑缁忛獙
        if (level >= MAX_LEVEL) {
            exp = 0;
            break;
        }
    }
    }

    

    /**
     * 鑾峰彇褰撳墠绛夌骇鍗囩骇杩涘害锛堢櫨鍒嗘瘮锛? * @return 鍗囩骇杩涘害锛?-100锛?
     */
    public float getLevelProgress() {
        if (level >= MAX_LEVEL) {
            return 100;
        }
        return (exp * 100.0f) / getExpForLevel(level);
    }

    /**
     * 鑾峰彇褰撳墠绛夌骇鍗囩骇杩橀渶瑕佺殑缁忛獙鍊? * @return 杩橀渶缁忛獙鍊?
     */
    public int getExpToNextLevel() {
        if (level >= MAX_LEVEL) {
            return 0;
        }
        return getExpForLevel(level) - exp;
    }

    /**
     * 鑾峰彇鍗囩骇鎵€闇€鐨勬€荤粡楠? * @return 鎬荤粡楠?
     */
    public int getTotalExpToNextLevel() {
        if (level >= MAX_LEVEL) {
            return 0;
        }
        return getExpForLevel(level);
    }
}
