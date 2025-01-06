package com.zjw.moreskill.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCapabilityPacket {
    private final int attackDamage;
    private final ItemStack itemStack;

    public SyncCapabilityPacket(int attackDamage, ItemStack itemStack) {
        this.attackDamage = attackDamage;
        this.itemStack = itemStack;
    }

    public static void encode(SyncCapabilityPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.attackDamage);
        buffer.writeItem(packet.itemStack);
    }

    public static SyncCapabilityPacket decode(FriendlyByteBuf buffer) {
        int attackDamage = buffer.readInt();
        ItemStack itemStack = buffer.readItem();
        return new SyncCapabilityPacket(attackDamage, itemStack);
    }

    public static void handle(SyncCapabilityPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // 在客户端更新 Capability 数据

        });
        context.get().setPacketHandled(true);
    }
}
