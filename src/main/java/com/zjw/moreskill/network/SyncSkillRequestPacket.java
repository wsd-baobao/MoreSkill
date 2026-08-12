package com.zjw.moreskill.network;

import com.zjw.moreskill.skill.CapabilityEventHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// 客户端打开技能面板时发送，服务端收到后回发全部技能数据
public class SyncSkillRequestPacket {
    public SyncSkillRequestPacket() {
    }

    public SyncSkillRequestPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public static void handle(SyncSkillRequestPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                CapabilityEventHandler.syncAllSkillsToPlayer(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
