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
        // 计算折扣（每升一级减少5%的消耗，最多减少99%）
        double discount = Math.min(0.99, playerLevel * 0.05); // 最多减少99%
        // 遍历所有交易选项
        for (int i = 0; i < offers.size(); i++) {
            MerchantOffer offer = offers.get(i);
            // 调整第一个购买物品的数量
            if (!offer.getCostA().isEmpty()) {
                int originalCount = offer.getCostA().getCount();
                int newCount = (int) Math.round(originalCount * (1 - discount));
                newCount = Math.max(1, newCount); // 确保至少为1
                int specialPriceDiff = newCount - originalCount; // 计算价格差异
                offer.setSpecialPriceDiff(specialPriceDiff); // 设置价格差异
            }
            // 调整第二个购买物品的数量
            if (!offer.getCostB().isEmpty()) {
                int originalCount = offer.getCostB().getCount();
                int newCount = (int) Math.round(originalCount * (1 - discount));
                newCount = Math.max(1, newCount); // 确保至少为1
                int specialPriceDiff = newCount - originalCount; // 计算价格差异
                offer.setSpecialPriceDiff(specialPriceDiff); // 设置价格差异
            }
        }
    }
}
