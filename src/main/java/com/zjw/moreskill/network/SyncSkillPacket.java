package com.zjw.moreskill.network;

import com.zjw.moreskill.skill.SkillRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSkillPacket {
    private final String skillKey;
    private final CompoundTag data;

    public SyncSkillPacket(String skillKey, CompoundTag data) {
        this.skillKey = skillKey;
        this.data = data;
    }

    public SyncSkillPacket(FriendlyByteBuf buf) {
        this.skillKey = buf.readUtf();
        this.data = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(skillKey);
        buf.writeNbt(data);
    }

    // 客户端接收：将技能数据写入客户端玩家的 capability（面板直接从该实例读取）
    public static void handle(SyncSkillPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || packet.data == null) return;
            Capability<? extends INBTSerializable<CompoundTag>> capability = SkillRegistry.getByKey(packet.skillKey);
            if (capability != null) {
                player.getCapability(capability).ifPresent(s -> s.deserializeNBT(packet.data));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
