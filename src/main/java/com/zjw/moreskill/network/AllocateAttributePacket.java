package com.zjw.moreskill.network;

import com.zjw.moreskill.attribute.AttributeData;
import com.zjw.moreskill.attribute.AttributeEffectHandler;
import com.zjw.moreskill.attribute.AttributeProvider;
import com.zjw.moreskill.attribute.ModAttribute;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AllocateAttributePacket {
    private final String attributeId;

    public AllocateAttributePacket(String attributeId) {
        this.attributeId = attributeId;
    }

    public AllocateAttributePacket(FriendlyByteBuf buf) {
        this.attributeId = buf.readUtf(256);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(attributeId, 256);
    }

    public static void handle(AllocateAttributePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ModAttribute attribute = ModAttribute.fromId(packet.attributeId);
            if (attribute == null) return;

            player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> {
                int currentPoints = data.getPoints(attribute);
                if (currentPoints >= attribute.getMaxPoints()) {
                    player.sendSystemMessage(
                            Component.translatable("message.moreskill.attribute.maxed", attribute.getDisplayName()));
                    return;
                }

                int cost = data.getCostForNextPoint(attribute);
                int playerLevels = player.experienceLevel;

                if (playerLevels < cost) {
                    player.sendSystemMessage(
                            Component.translatable("message.moreskill.attribute.not_enough_xp", cost, playerLevels));
                    return;
                }

                player.giveExperienceLevels(-cost);
                data.allocate(attribute);
                AttributeEffectHandler.applyAllModifiers(player);

                player.sendSystemMessage(
                        Component.translatable("message.moreskill.attribute.allocated",
                                attribute.getDisplayName(), currentPoints + 1));

                SyncAttributePacket.syncToPlayer(player);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
