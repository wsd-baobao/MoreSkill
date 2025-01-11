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
import com.zjw.moreskill.skill.woodcutting.WoodCuttingProvider;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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
        deserializeSkill(player, FishingSkillProvider.FISHING_SKILL, "fishing_skill");
        deserializeSkill(player, MiningSkillProvider.MINING_SKILL, "mining_skill");
        deserializeSkill(player, SmithingSkillProvider.SMITHING_SKILL, "smithing_skill");
        deserializeSkill(player, FarmingProvider.FARMING_CAPABILITY, "farming_skill");
        deserializeSkill(player, CookingProvider.COOKING_CAPABILITY, "cooking_skill");
        deserializeSkill(player, CombatProvider.COMBAT_CAPABILITY, "combat_skill");
        deserializeSkill(player, AlchemyProvider.ALCHEMY_CAPABILITY, "alchemy_skill");
        deserializeSkill(player, TradingProvider.TRADING_CAPABILITY, "trading_skill");
        deserializeSkill(player, WoodCuttingProvider.WOODCUTTING_CAPABILITY, "woodcutting_skill");
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

   
     /**
     * 从存档文件中加载技能数据
     */
    private void deserializeSkill(Player player, Capability<? extends INBTSerializable<CompoundTag>> skillCapability, String skillKey) {

        if (player.level().isClientSide) {
            return; // 在客户端不执行保存逻辑
        }

        Path savePath = player.level().getServer().getWorldPath(LevelResource.ROOT).resolve("moreskill");
        File skillFile = savePath.resolve(player.getUUID().toString() + "_" + skillKey + ".dat").toFile();

        if (skillFile.exists()) {
            try {
                CompoundTag nbt = NbtIo.readCompressed(skillFile);
                if (nbt != null) {
                    player.getCapability(skillCapability).ifPresent(skill -> skill.deserializeNBT(nbt));
                }
            } catch (IOException e) {
                logger.error("Failed to load skill data for {}: {}", skillKey, e.getMessage());
            }
        } else {
            // 文件不存在时，初始化默认数据
            logger.info("Skill data file not found for {}: {}. Initializing default data.", skillKey, skillFile.getPath());
            player.getCapability(skillCapability).ifPresent(skill -> {
                skill.deserializeNBT(new CompoundTag()); // 初始化默认数据
                saveSkillData(player, skillCapability, skillKey); // 保存默认数据
            });
        }
    }

      /**
     * 将技能数据保存到存档文件中
     */
    private void saveSkillData(Player player, Capability<? extends INBTSerializable<CompoundTag>> skillProvider, String skillKey) {
        if (player.level().isClientSide) {
            return; // 在客户端不执行保存逻辑
        }
        Path savePath = player.level().getServer().getWorldPath(LevelResource.ROOT).resolve("moreskill");
        File skillFile = savePath.resolve(player.getUUID().toString() + "_" + skillKey + ".dat").toFile();

        // 确保文件夹存在
        skillFile.getParentFile().mkdirs();
        player.getCapability(skillProvider).ifPresent(skill -> {
            CompoundTag nbt = skill.serializeNBT();
            try {
                NbtIo.writeCompressed(nbt, skillFile);
            } catch (IOException e) {
                logger.error("Failed to save skill data for {}: {}", skillKey, e.getMessage());
            }
        });
    }
}
