package com.zjw.moreskill.skill.trading;

import com.zjw.moreskill.MoreSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TradingHandler {
    @SubscribeEvent
    public void onContainerOpen(PlayerContainerEvent.Open event) {
        // 检查是否是村民交易界面
        if (event.getContainer() instanceof MerchantMenu merchantMenu) {
            Player player = event.getEntity();
            // 获取村民的交易选项
            MerchantOffers offers = merchantMenu.getOffers();
            // 获取玩家等级（假设你有一个能力系统或等级系统）
            int playerLevel = player.getCapability(TradingProvider.TRADING_CAPABILITY).map(Trading::getLevel).orElse(0);
            // 根据玩家等级调整交易消耗
            adjustTradeCosts(offers, playerLevel);
        }
    }
    // 调整交易消耗
    private void adjustTradeCosts(MerchantOffers offers, int playerLevel) {
        // 计算折扣（每升一级减少5%的消耗，最多减少100%）
        double discount = Math.max(0.01, 1.0 - (playerLevel * 0.05));
        // 遍历所有交易选项
        for (int i = 0; i < offers.size(); i++) {
            MerchantOffer offer = offers.get(i);
            // 获取交易物品
            ItemStack buyItem1 = offer.getCostA().copy(); // 创建新的 ItemStack
            ItemStack buyItem2 = offer.getCostB().copy(); // 创建新的 ItemStack
            ItemStack sellItem = offer.getResult().copy(); // 创建新的 ItemStack
            // 调整第一个购买物品的数量
            if (!buyItem1.isEmpty()) {
                int newCount = (int) Math.round(buyItem1.getCount() * discount);
                buyItem1.setCount(Math.max(1, newCount)); // 确保至少为1
            }
            // 调整第二个购买物品的数量
            if (!buyItem2.isEmpty()) {
                int newCount = (int) Math.round(buyItem2.getCount() * discount);
                buyItem2.setCount(Math.max(1, newCount)); // 确保至少为1
            }
            // 创建新的 MerchantOffer
            MerchantOffer newOffer = new MerchantOffer(
                    buyItem1, // 第一个购买物品
                    buyItem2, // 第二个购买物品
                    sellItem, // 出售物品
                    offer.getUses(), // 当前使用次数
                    offer.getMaxUses(), // 最大使用次数
                    offer.getXp(), // 交易经验
                    offer.getPriceMultiplier(), // 价格乘数
                    offer.getDemand() // 需求
            );
            // 替换旧的交易选项
            offers.set(i, newOffer);
        }
    }
}
