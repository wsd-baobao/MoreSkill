package com.zjw.moreskill.mixin.trading;

import com.zjw.moreskill.skill.trading.TradingProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class TradingAddExp {

    @Inject(method = "rewardTradeXp" ,at = @At("HEAD"))
    private void addTradingExp(MerchantOffer merchantOffer, CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;
        Player player = villager.getTradingPlayer();
        if (player != null && !villager.level().isClientSide()) {
            player.getCapability(TradingProvider.TRADING_CAPABILITY).ifPresent(trading -> {
                int expGain = 3;
                trading.addExp(expGain);
            });
        }
    }
}
