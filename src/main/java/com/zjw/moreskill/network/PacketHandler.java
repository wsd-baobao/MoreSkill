package com.zjw.moreskill.network;

import com.zjw.moreskill.skill.fishing.FishingSkillProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketHandler {
     private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation("fishingskills", "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void register() {
        CHANNEL.registerMessage(0, SyncFishingSkillPacket.class, SyncFishingSkillPacket::encode,
                SyncFishingSkillPacket::decode, SyncFishingSkillPacket::handle);
        
    }
}

class SyncFishingSkillPacket {
    private final CompoundTag data;

    public SyncFishingSkillPacket(CompoundTag data) {
        this.data = data;
    }

    public static void encode(SyncFishingSkillPacket msg, FriendlyByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static SyncFishingSkillPacket decode(FriendlyByteBuf buf) {
        return new SyncFishingSkillPacket(buf.readNbt());
    }

    public static void handle(SyncFishingSkillPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getCapability(FishingSkillProvider.FISHING_SKILL).ifPresent(skill -> {
//                    FishingSkillProvider(skill, msg.data);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
