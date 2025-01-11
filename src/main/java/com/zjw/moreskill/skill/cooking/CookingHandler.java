package com.zjw.moreskill.skill.cooking;

import com.zjw.moreskill.MoreSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CookingHandler {

    //合成获取经验
    @SubscribeEvent
    public void onItemCrafted(ItemCraftedEvent event) {
        Player player = event.getEntity();
        ItemStack craftedItem = event.getCrafting();
        if (craftedItem.isEdible()) {
            player.getCapability(CookingProvider.COOKING_CAPABILITY).ifPresent(cooking -> {
                cooking.addCookingExp(player, 1);
            });
        }
    }

    //熔炉获取经验
    @SubscribeEvent
    public void onItemSmelt(PlayerEvent.ItemSmeltedEvent event) {
        Player player = event.getEntity();
        ItemStack smeltedItem = event.getSmelting();
        if (smeltedItem.isEdible()) {
            player.getCapability(CookingProvider.COOKING_CAPABILITY).ifPresent(cooking -> {
                cooking.addCookingExp(player, 5);
            });
        }
    }
    //吃东西获取加成，todo 直接修改食物的属性  buhui
    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack itemStack = event.getItem();

            if (itemStack.isEdible()) {
                player.getCapability(CookingProvider.COOKING_CAPABILITY)
                        .map(cooking -> {

                            float extraNutrition = cooking.calculateExtraNutrition();
                            float extraSaturation = cooking.calculateExtraSaturation();

                            int originalNutrition = itemStack.getFoodProperties(player).getNutrition();
                            float originalSaturation = itemStack.getFoodProperties(player).getSaturationModifier();

                            float finalNutrition = originalNutrition + extraNutrition;
                            float finalSaturation = originalSaturation + extraSaturation;

                            player.getFoodData().eat(
                                    (int) finalNutrition,
                                    finalSaturation
                            );
//                            MoreSkill.LOGGER.debug("Cooking Skill Level: {}, Extra Nutrition: {}, Extra Saturation: {}",
//                                    cooking.getLevel(), extraNutrition, extraSaturation);
                            return cooking.getLevel();
                        })
                        .orElse(0);
            }
        }
    }
}
