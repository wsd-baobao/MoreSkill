package com.zjw.moreskill.skill;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.skill.alchemy.AlchemyProvider;
import com.zjw.moreskill.skill.combat.CombatProvider;
import com.zjw.moreskill.skill.cooking.CookingProvider;
import com.zjw.moreskill.skill.farming.FarmingProvider;
import com.zjw.moreskill.skill.fishing.FishingSkillProvider;
import com.zjw.moreskill.skill.mining.MiningSkillProvider;
import com.zjw.moreskill.skill.smithing.SmithingSkillProvider;
import com.zjw.moreskill.skill.trading.TradingProvider;
import com.zjw.moreskill.skill.woodcutting.WoodCutting;
import com.zjw.moreskill.skill.woodcutting.WoodCuttingProvider;

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

//澶勭悊鑳藉姏鐨勫姞杞戒笌淇濆瓨

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityEventHandler {
    private static final Logger logger = LogManager.getLogger();

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "fishing_skill"), new FishingSkillProvider());
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "mining_skill"), new MiningSkillProvider());
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "smithing_skill"), new SmithingSkillProvider());
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "farming_skill"), new FarmingProvider());
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "cooking_skill"), new CookingProvider());
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "combat_skill"), new CombatProvider());
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "alchemy_skill"), new AlchemyProvider());
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "trading_skill"), new TradingProvider());
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "woodcutting_skill"), new WoodCuttingProvider());
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
        deserializeSkill(player, FarmingProvider.FARMING_CAPABILITY, persistentData, "farming_skill");
        deserializeSkill(player, CookingProvider.COOKING_CAPABILITY, persistentData, "cooking_skill");
        deserializeSkill(player, CombatProvider.COMBAT_CAPABILITY, persistentData, "combat_skill");
        deserializeSkill(player, AlchemyProvider.ALCHEMY_CAPABILITY, persistentData, "alchemy_skill");
        deserializeSkill(player, TradingProvider.TRADING_CAPABILITY, persistentData, "trading_skill");
        deserializeSkill(player, WoodCuttingProvider.WOODCUTTING_CAPABILITY, persistentData, "woodcutting_skill");
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        saveSkillData(player, FishingSkillProvider.FISHING_SKILL, "fishing_skill");
        saveSkillData(player, MiningSkillProvider.MINING_SKILL, "mining_skill");
        saveSkillData(player, SmithingSkillProvider.SMITHING_SKILL, "smithing_skill");
        saveSkillData(player, FarmingProvider.FARMING_CAPABILITY, "farming_skill");
        saveSkillData(player, CookingProvider.COOKING_CAPABILITY, "cooking_skill");
        saveSkillData(player, CombatProvider.COMBAT_CAPABILITY, "combat_skill");
        saveSkillData(player, AlchemyProvider.ALCHEMY_CAPABILITY, "alchemy_skill");
        saveSkillData(player, TradingProvider.TRADING_CAPABILITY, "trading_skill");
        saveSkillData(player, WoodCuttingProvider.WOODCUTTING_CAPABILITY, "woodcutting_skill");
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().getGameTime() % 12000 == 0) {
            Player player = event.player;
            saveSkillData(player, FishingSkillProvider.FISHING_SKILL, "fishing_skill");
            saveSkillData(player, MiningSkillProvider.MINING_SKILL, "mining_skill");
            saveSkillData(player, SmithingSkillProvider.SMITHING_SKILL, "smithing_skill");
            saveSkillData(player, FarmingProvider.FARMING_CAPABILITY, "farming_skill");
            saveSkillData(player, CookingProvider.COOKING_CAPABILITY, "cooking_skill");
            saveSkillData(player, CombatProvider.COMBAT_CAPABILITY, "combat_skill");
            saveSkillData(player, AlchemyProvider.ALCHEMY_CAPABILITY, "alchemy_skill");
            saveSkillData(player, TradingProvider.TRADING_CAPABILITY, "trading_skill");
            saveSkillData(player, WoodCuttingProvider.WOODCUTTING_CAPABILITY, "woodcutting_skill");
        }
    }

    private void deserializeSkill(Player player, Capability<? extends INBTSerializable<CompoundTag>> skillCapability,
            CompoundTag persistentData, String skillKey) {
        player.getCapability(skillCapability).ifPresent(skill -> {
            if (persistentData.contains(skillKey, Tag.TAG_COMPOUND)) {
                skill.deserializeNBT(persistentData.getCompound(skillKey));
            } else {
                logger.warn("Missing skill data for key: {}", skillKey);
            }
        });
    }

    private void saveSkillData(Player player, Capability<? extends INBTSerializable<CompoundTag>> skillProvider,
            String key) {
        player.getCapability(skillProvider).ifPresent(skill -> {
            CompoundTag compoundTag = skill.serializeNBT();
            player.getPersistentData().put(key, compoundTag);
            MoreSkill.LOGGER.info("Saved skill data for key: {}", key);
        });
    }
}
