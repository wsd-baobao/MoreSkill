package com.zjw.moreskill.network;

import com.zjw.moreskill.skill.alchemy.AlchemyProvider;
import com.zjw.moreskill.skill.combat.CombatProvider;
import com.zjw.moreskill.skill.cooking.CookingProvider;
import com.zjw.moreskill.skill.farming.FarmingProvider;
import com.zjw.moreskill.skill.fishing.FishingSkillProvider;
import com.zjw.moreskill.skill.mining.MiningSkillProvider;
import com.zjw.moreskill.skill.smithing.SmithingSkillProvider;
import com.zjw.moreskill.skill.trading.TradingProvider;
import com.zjw.moreskill.skill.woodcutting.WoodCuttingProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
            switch (packet.skillKey) {
                case "fishing_skill" -> player.getCapability(FishingSkillProvider.FISHING_SKILL).ifPresent(s -> s.deserializeNBT(packet.data));
                case "mining_skill" -> player.getCapability(MiningSkillProvider.MINING_SKILL).ifPresent(s -> s.deserializeNBT(packet.data));
                case "smithing_skill" -> player.getCapability(SmithingSkillProvider.SMITHING_SKILL).ifPresent(s -> s.deserializeNBT(packet.data));
                case "farming_skill" -> player.getCapability(FarmingProvider.FARMING_CAPABILITY).ifPresent(s -> s.deserializeNBT(packet.data));
                case "cooking_skill" -> player.getCapability(CookingProvider.COOKING_CAPABILITY).ifPresent(s -> s.deserializeNBT(packet.data));
                case "combat_skill" -> player.getCapability(CombatProvider.COMBAT_CAPABILITY).ifPresent(s -> s.deserializeNBT(packet.data));
                case "alchemy_skill" -> player.getCapability(AlchemyProvider.ALCHEMY_CAPABILITY).ifPresent(s -> s.deserializeNBT(packet.data));
                case "trading_skill" -> player.getCapability(TradingProvider.TRADING_CAPABILITY).ifPresent(s -> s.deserializeNBT(packet.data));
                case "woodcutting_skill" -> player.getCapability(WoodCuttingProvider.WOODCUTTING_CAPABILITY).ifPresent(s -> s.deserializeNBT(packet.data));
                default -> { }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
