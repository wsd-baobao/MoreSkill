package com.zjw.moreskill.skill.mining;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Random;

/**
 * 挖矿技能
 * todo 提高挖掘速度 (写不出来)
 * 额外的资源获取
 * 额外的经验获取
 *  *                       20    40      10   50   80
 *  * 到达一定的等级后在地下提供ji po生命恢复，抗火，饱和，抗性等buff（可以分阶段提供）
 * <p>
 * 范围的矿物提示框（满级50-100格） pass
 * 减少体力消耗 pass
 * 满级通过基岩采矿 不会
 */


public class Mining implements INBTSerializable<CompoundTag> {

    private int level = 0;
    private int exp = 0;
    private static final int MAX_LEVEL = 100;

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
        return Component.translatable("skill.moreskill.mining");
    }

    private void setExp(int experience) {
        this.exp = experience;
    }

    private void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getExpForNextLevel() {
        return 100 + (this.level * 300);
    }

    public void addExp(Player player, int expGain) {
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

    public int getItemsCountByLevel() {
        return new Random().nextInt(Math.min(11, (level / 10) + 2));
    }
}
