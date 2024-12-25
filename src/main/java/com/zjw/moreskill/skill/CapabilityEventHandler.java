package com.zjw.moreskill.skill;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.skill.fishing.FishingSkillProvider;
import com.zjw.moreskill.skill.mining.MiningSkillProvider;
import com.zjw.moreskill.skill.smithing.SmithingSkillProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//处理能力的加载与保存

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityEventHandler {
    private static final Logger logger = LogManager.getLogger();

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "fishing_skill"), new FishingSkillProvider());
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "mining_skill"), new MiningSkillProvider());
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "smithing_skill"), new SmithingSkillProvider());
        }
    }
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        System.out.println("onPlayerLoggedIn");
        Player player = event.getEntity();
        CompoundTag persistentData = player.getPersistentData();
        deserializeSkill(player, FishingSkillProvider.FISHING_SKILL, persistentData, "fishing_skill");
        deserializeSkill(player, MiningSkillProvider.MINING_SKILL, persistentData, "mining_skill");
        deserializeSkill(player, SmithingSkillProvider.SMITHING_SKILL, persistentData, "smithing_skill");
    }
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        saveSkillData(player, FishingSkillProvider.FISHING_SKILL, "fishing_skill");
        saveSkillData(player, MiningSkillProvider.MINING_SKILL, "mining_skill");
        saveSkillData(player, SmithingSkillProvider.SMITHING_SKILL, "smithing_skill");
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().getGameTime() % 2400 == 0) {
            Player player = event.player;
            saveSkillData(player, FishingSkillProvider.FISHING_SKILL, "fishing_skill");
            saveSkillData(player, MiningSkillProvider.MINING_SKILL, "mining_skill");
            saveSkillData(player, SmithingSkillProvider.SMITHING_SKILL, "smithing_skill");
        }
    }

    private void deserializeSkill(Player player, Capability<? extends INBTSerializable<CompoundTag>> skillCapability, CompoundTag persistentData, String skillKey) {
        player.getCapability(skillCapability).ifPresent(skill -> {
            if (persistentData.contains(skillKey, Tag.TAG_COMPOUND)) {
                skill.deserializeNBT(persistentData.getCompound(skillKey));
            } else {
                logger.warn("Missing skill data for key: {}", skillKey);
            }
        });
    }

    private void saveSkillData(Player player, Capability<? extends INBTSerializable<CompoundTag>> skillProvider, String key) {
        player.getCapability(skillProvider).ifPresent(skill -> {
            CompoundTag compoundTag = skill.serializeNBT();
            player.getPersistentData().put(key, compoundTag);
            // 添加日志记录
            System.out.println("Saved " + key + " data for player: " + player.getName().getString());
        });
    }
}
