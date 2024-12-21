package com.zjw.moreskill.skill.fishing;

import com.zjw.moreskill.MoreSkill;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


// 监听玩家钓鱼事件(处理玩家的钓鱼相关行为)
@Mod.EventBusSubscriber(modid = MoreSkill.MODID,bus=Mod.EventBusSubscriber.Bus.FORGE)
public class FishingHandler {

    // 缓存玩家的FishingPoolManager实例
    private static final ConcurrentHashMap<UUID, FishingPoolManager> playerPools = new ConcurrentHashMap<>();
    // 监听玩家加入事件，在玩家进入游戏时初始化FishingPoolManager
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUUID();
        System.out.println(playerId);
        playerPools.putIfAbsent(playerId, new FishingPoolManager());
    }
    public static FishingPoolManager getPlayerPool(UUID playerId) {
        return playerPools.getOrDefault(playerId, null);
    }
    @SubscribeEvent
    public void onPlayerFish(ItemFishedEvent event) {
        Player player = event.getEntity();
        player.getCapability(FishingSkillProvider.FISHING_SKILL).ifPresent(fishing -> {
            List<ItemStack> drops = event.getDrops();
            Random random = new Random();
            System.out.println(player.getUUID());
            drops.addAll(getPlayerPool(player.getUUID()).getRandomItems(fishing.getLevel(), random, fishing.numberOfItemsToFish()));
            // 创建并添加 ItemEntity 到世界中
            for (int i = 1; i < drops.size(); i++) {
                ItemStack extraDrop = drops.get(i);
                ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), extraDrop);
                player.level().addFreshEntity(itemEntity);
                player.level().addFreshEntity(new ExperienceOrb(player.level(), player.getX(), player.getY(), player.getZ(), 1));
            }
            // 计算经验值（这里每个物品给1点经验）
            int experienceGained = drops.size();
            // 增加经验
            fishing.addExp(player, experienceGained);
            System.out.println("经验增加了");
            System.out.println(fishing.getExp());
        });

    }


}
