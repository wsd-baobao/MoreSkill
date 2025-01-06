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

    @SubscribeEvent
    public void onItemCrafted(ItemCraftedEvent event) {
        Player player =  event.getEntity();
        ItemStack craftedItem = event.getCrafting();

        // 妫€鏌ュ悎鎴愮殑鐗╁搧鏄惁鏄鐗?      
          if (craftedItem.isEdible()) {
            player.getCapability(CookingProvider.COOKING_CAPABILITY).ifPresent(cooking -> {
                // 涓洪鐗╁悎鎴愭坊鍔犵粡楠?              
                  cooking.addCookingExp(player, 2);
            });
        }
    }

    @SubscribeEvent
    public void onItemSmelt(PlayerEvent.ItemSmeltedEvent event) {
        Player player = (Player) event.getEntity();
        ItemStack smeltedItem = event.getSmelting();

           if (smeltedItem.isEdible()) {
            player.getCapability(CookingProvider.COOKING_CAPABILITY).ifPresent(cooking -> {
                           cooking.addCookingExp(player, 5);
            });
        }
    }

     @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack itemStack = event.getItem();
            
            // 鍙鐞嗛鐗╃被鍨嬬殑鐗╁搧
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

                        // 鍙€夛細娣诲姞鏃ュ織鎴栧叾浠栨晥鏋?             
                                   MoreSkill.LOGGER.debug("Cooking Skill Level: {}, Extra Nutrition: {}, Extra Saturation: {}", 
                            cooking.getLevel(), extraNutrition, extraSaturation);

                        return cooking.getLevel();
                    })
                    .orElse(0);
            }
        }
    }
}
