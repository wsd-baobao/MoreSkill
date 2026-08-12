package com.zjw.moreskill.network;

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
                if (data.getAvailablePoints() <= 0) {
                    player.sendSystemMessage(
                            Component.translatable("message.moreskill.attribute.no_points_available"));
                    return;
                }

                int currentPoints = data.getPoints(attribute);
                if (currentPoints >= attribute.getMaxPoints()) {
                    player.sendSystemMessage(
                            Component.translatable("message.moreskill.attribute.maxed", attribute.getDisplayName()));
                    return;
                }

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
