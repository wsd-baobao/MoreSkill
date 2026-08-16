package com.zjw.moreskill.skill.mining;

import com.zjw.moreskill.skill.AbstractSkill;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

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
public class Mining extends AbstractSkill {

    @Override
    public Component getName() {
        return Component.translatable("skill.moreskill.mining");
    }

    @Override
    public int getExpForNextLevel() {
        return 100 + (getLevel() * 300);
    }

    public void addExp(Player player, int expGain) {
        addExp(expGain);
    }

    public int getItemsCountByLevel() {
        return new Random().nextInt(Math.min(11, (getLevel() / 10) + 2));
    }
}