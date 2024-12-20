package com.zjw.moreskill.skill.fishing;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

// 监听玩家钓鱼事件(处理玩家的钓鱼相关行为)

public class FishingHandler {

    @SubscribeEvent
    public void onPlayerFish(ItemFishedEvent event) {
        // 获取玩家
        Player player = event.getEntity();

        player.getCapability(FishingSkillProvider.FISHING_SKILL).ifPresent(fishing -> {
            List<ItemStack> drops = event.getDrops();
            Random random = new Random();
//            for (int i = 0; i < 10; i++) {
//                if (!drops.isEmpty()) {
//                    ItemStack extraDrop;
//                    if (random.nextBoolean()) {
//                        // 随机选择已有的一个物品作为额外掉落
//                        extraDrop = drops.get(random.nextInt(drops.size())).copy();
//                    } else {
//                        // 或者选择一个新的物品（例如不同类型的鱼）
//                        extraDrop = getRandomFishItemStack(player.level().random);
//                    }
//                    extraDrop.setCount(1); // 确保只复制一个单位

                    FishingPoolManager.getInstance().getRandomItems(100, random, 10).forEach(
                            item -> drops.add(new ItemStack(item))
                    );

                    // 创建并添加 ItemEntity 到世界中
                    for (int i =1; i< drops.size();i++){
                        ItemStack extraDrop = drops.get(i);

                        ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(),extraDrop );
                        player.level().addFreshEntity(itemEntity);
                    }

//                    drops.add(extraDrop);
//                }
//            }
            // 计算经验值（这里假设每个物品给1点经验，您可以根据需求调整）
            int experienceGained = drops.size();
            // 增加经验
            fishing.addExp(player, experienceGained);

            System.out.println("zengjiale");
            System.out.println(fishing.getExp());
        });
        // 获取钓到的物品
    }

    private static ItemStack getRandomFishItemStack(RandomSource rand) {
        // 这里可以根据你的模组内容返回不同的鱼或其他物品
        // 例如，从预定义的物品列表中随机选择一个
        List<Item> fishItems = Arrays.asList(
                Items.COD,
                Items.SALMON,
                Items.TROPICAL_FISH,
                Items.PUFFERFISH
        );
        return new ItemStack(fishItems.get(rand.nextInt(fishItems.size())));
    }



}
