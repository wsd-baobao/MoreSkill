package com.zjw.moreskill.network;

import java.util.function.Supplier;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.entity.ModEntities;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class MoreSkillIronGolemPacket {
    public MoreSkillIronGolemPacket() {
    }

    public MoreSkillIronGolemPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public static void handle(MoreSkillIronGolemPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            MoreSkill.LOGGER.info(player.toString());

            if (player == null)
                return;

            Level level = player.level();
            var golem = ModEntities.MORE_SKILL_IRON_GOLEM.get().create(level);
            if (golem != null) {
                golem.setOwner(player.getUUID());
                golem.setDespawnTicks(20000); // 120秒
                golem.setPos(player.getX(), player.getY(), player.getZ());
                golem.setPersistenceRequired();
                MoreSkill.LOGGER.info("MoreSkillIronGolemPacket: summon iron golem{}", golem);
                level.addFreshEntity(golem);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
