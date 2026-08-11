package com.zjw.moreskill.network;

import com.zjw.moreskill.attribute.AttributeData;
import com.zjw.moreskill.attribute.AttributeProvider;
import com.zjw.moreskill.attribute.ModAttribute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncAttributePacket {
    private final CompoundTag data;

    public SyncAttributePacket(CompoundTag data) {
        this.data = data;
    }

    public SyncAttributePacket(FriendlyByteBuf buf) {
        this.data = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(data);
    }

    public static void handle(SyncAttributePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;

            player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(clientData -> {
                clientData.deserializeNBT(packet.data);
            });
        });
        ctx.get().setPacketHandled(true);
    }

    public static void syncToPlayer(ServerPlayer player) {
        player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> {
            CompoundTag tag = data.serializeNBT();
            NetworkHandler.INSTANCE.sendTo(
                    new SyncAttributePacket(tag),
                    player.connection.connection,
                    net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
        });
    }
}
