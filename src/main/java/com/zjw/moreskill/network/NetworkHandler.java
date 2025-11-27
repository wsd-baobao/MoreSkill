package com.zjw.moreskill.network;

import com.zjw.moreskill.MoreSkill;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MoreSkill.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void register() {
        int packetId = 0;
        INSTANCE.registerMessage(packetId++, SyncCapabilityPacket.class, SyncCapabilityPacket::encode,
                SyncCapabilityPacket::decode, SyncCapabilityPacket::handle);
        INSTANCE.messageBuilder(MoreSkillIronGolemPacket.class, packetId++)
                .encoder(MoreSkillIronGolemPacket::toBytes)
                .decoder(MoreSkillIronGolemPacket::new)
                .consumerMainThread(MoreSkillIronGolemPacket::handle)
                .add();
    }
}
