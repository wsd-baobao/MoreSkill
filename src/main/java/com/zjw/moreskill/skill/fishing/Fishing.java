package com.zjw.moreskill.skill.fishing;

import com.zjw.moreskill.skill.AbstractSkill;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

/*
 * 钓鱼技能类
 */
public class Fishing extends AbstractSkill {

    @Override
    public Component getName() {
        return Component.translatable("skill.moreskill.fishing");
    }

    public void addExp(Player player, int expGain) {
        addExp(expGain);
    }

    public int numberOfItemsToFish() {
        return new Random().nextInt(Math.min(11, (getLevel() / 10) + 2));
    }

    @Override
    public int getExpForNextLevel() {
        return 100 + (getLevel() * 50);
    }
}