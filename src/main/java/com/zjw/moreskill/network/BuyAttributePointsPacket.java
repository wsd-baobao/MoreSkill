package com.zjw.moreskill.network;

import com.zjw.moreskill.attribute.AttributeProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BuyAttributePointsPacket {
    private final int xpAmount;

    public BuyAttributePointsPacket(int xpAmount) {
        this.xpAmount = xpAmount;
    }

    public BuyAttributePointsPacket(FriendlyByteBuf buf) {
        this.xpAmount = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(xpAmount);
    }

    public static void handle(BuyAttributePointsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            int xpAmount = packet.xpAmount;
            if (xpAmount <= 0) return;

            int totalXp = getTotalExperience(player);
            if (totalXp < xpAmount) {
                player.sendSystemMessage(Component.translatable("message.moreskill.attribute.not_enough_xp_points"));
                return;
            }

            player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> {
                int cost = data.getCostForNextPoint();
                if (totalXp < cost) {
                    player.sendSystemMessage(
                            Component.translatable("message.moreskill.attribute.need_more_xp", cost));
                    return;
                }

                int beforeBought = data.getTotalPointsBought();
                data.buyPoints(xpAmount);
                int bought = data.getTotalPointsBought() - beforeBought;

                int actualCost = data.getXpForBuyAmount(bought);
                int remaining = totalXp - actualCost;
                setTotalExperience(player, remaining);

                player.sendSystemMessage(
                        Component.translatable("message.moreskill.attribute.points_bought", bought, actualCost));

                SyncAttributePacket.syncToPlayer(player);
            });
        });
        ctx.get().setPacketHandled(true);
    }

    public static int getTotalExperience(Player player) {
        int level = player.experienceLevel;
        int total;
        if (level <= 16) {
            total = level * level + 6 * level;
        } else if (level <= 31) {
            total = (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            total = (int) (4.5 * level * level - 162.5 * level + 2220);
        }
        total += (int) (player.experienceProgress * player.getXpNeededForNextLevel());
        return total;
    }

    private static void setTotalExperience(ServerPlayer player, int newTotal) {
        player.totalExperience = 0;
        player.experienceLevel = 0;
        player.experienceProgress = 0;
        if (newTotal > 0) {
            player.giveExperiencePoints(newTotal);
        }
    }
}
