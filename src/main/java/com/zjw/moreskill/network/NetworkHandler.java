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
        INSTANCE.messageBuilder(AllocateAttributePacket.class, packetId++)
                .encoder(AllocateAttributePacket::toBytes)
                .decoder(AllocateAttributePacket::new)
                .consumerMainThread(AllocateAttributePacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncAttributePacket.class, packetId++)
                .encoder(SyncAttributePacket::toBytes)
                .decoder(SyncAttributePacket::new)
                .consumerMainThread(SyncAttributePacket::handle)
                .add();
        INSTANCE.messageBuilder(BuyAttributePointsPacket.class, packetId++)
                .encoder(BuyAttributePointsPacket::toBytes)
                .decoder(BuyAttributePointsPacket::new)
                .consumerMainThread(BuyAttributePointsPacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncSkillRequestPacket.class, packetId++)
                .encoder(SyncSkillRequestPacket::toBytes)
                .decoder(SyncSkillRequestPacket::new)
                .consumerMainThread(SyncSkillRequestPacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncSkillPacket.class, packetId++)
                .encoder(SyncSkillPacket::toBytes)
                .decoder(SyncSkillPacket::new)
                .consumerMainThread(SyncSkillPacket::handle)
                .add();
    }
}
