package com.zjw.moreskill.skill;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.attribute.AttributeEffectHandler;
import com.zjw.moreskill.network.NetworkHandler;
import com.zjw.moreskill.network.SyncAttributePacket;
import com.zjw.moreskill.network.SyncSkillPacket;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// 处理能力的加载与保存

public class CapabilityEventHandler {
    private static final Logger logger = LogManager.getLogger();

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            SkillRegistry.getEntries().forEach(entry ->
                    event.addCapability(
                            new ResourceLocation(MoreSkill.MODID, entry.key()),
                            entry.providerFactory().get()));
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        SkillRegistry.getEntries().forEach(entry -> deserializeSkill(player, entry.capability(), entry.key()));
        AttributeEffectHandler.applyAllModifiers(player);
        if (player instanceof ServerPlayer serverPlayer) {
            SyncAttributePacket.syncToPlayer(serverPlayer);
            syncAllSkillsToPlayer(serverPlayer);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        SkillRegistry.getEntries().forEach(entry -> saveSkillData(player, entry.capability(), entry.key()));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().getGameTime() % 12000 == 0) {
            Player player = event.player;
            SkillRegistry.getEntries().forEach(entry -> saveSkillData(player, entry.capability(), entry.key()));
        }
        // 定期向客户端同步技能数据（约每10秒一次）
        if (event.player.level().getGameTime() % 200 == 0 && event.player instanceof ServerPlayer serverPlayer) {
            syncAllSkillsToPlayer(serverPlayer);
        }
    }

    /**
     * 将全部技能数据同步给指定玩家（登录、打开面板、定期刷新时调用）
     */
    public static void syncAllSkillsToPlayer(ServerPlayer player) {
        SkillRegistry.getEntries().forEach(entry -> {
            if (entry.key().equals("attributes")) {
                return; // 属性数据由 SyncAttributePacket 同步
            }
            player.getCapability(entry.capability()).ifPresent(skill -> {
                CompoundTag nbt = skill.serializeNBT();
                NetworkHandler.INSTANCE.sendTo(new SyncSkillPacket(entry.key(), nbt),
                        player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            });
        });
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
