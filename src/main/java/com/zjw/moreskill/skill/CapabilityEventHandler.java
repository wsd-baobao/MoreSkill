package com.zjw.moreskill.skill;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.skill.fishing.FishingSkillProvider;
import com.zjw.moreskill.skill.mining.MiningSkillProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//处理能力的加载与保存

@Mod.EventBusSubscriber(modid = MoreSkill.MODID,bus=Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityEventHandler {
    @SubscribeEvent
    public  void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "fishing_skill"), new FishingSkillProvider());
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "mining_skill"), new MiningSkillProvider());
        }
    }

    @SubscribeEvent
    public  void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        System.out.println("onPlayerLoggedIn");
        Player player = event.getEntity();
        player.getCapability(FishingSkillProvider.FISHING_SKILL).ifPresent(skill -> {
            skill.deserializeNBT(event.getEntity().getPersistentData().getCompound("fishing_skill"));
        });
        player.getCapability(MiningSkillProvider.MINING_SKILL).ifPresent(skill -> {
            skill.deserializeNBT(event.getEntity().getPersistentData().getCompound("mining_skill"));
        });
    }
    @SubscribeEvent
    public  void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        player.getCapability(FishingSkillProvider.FISHING_SKILL).ifPresent(skill -> {
            CompoundTag compoundTag = skill.serializeNBT();
            event.getEntity().getPersistentData().put("fishing_skill", compoundTag);
        });
        player.getCapability(MiningSkillProvider.MINING_SKILL).ifPresent(skill -> {
            CompoundTag compoundTag = skill.serializeNBT();
            event.getEntity().getPersistentData().put("mining_skill", compoundTag);
        });
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().getGameTime() % 2400 == 0){
            Player player = event.player;
            player.getCapability(FishingSkillProvider.FISHING_SKILL).ifPresent(skill -> {
                CompoundTag compoundTag = skill.serializeNBT();
                player.getPersistentData().put("fishing_skill", compoundTag);
            });
            player.getCapability(MiningSkillProvider.MINING_SKILL).ifPresent(skill -> {
                CompoundTag compoundTag = skill.serializeNBT();
                player.getPersistentData().put("mining_skill", compoundTag);
            });
        }
   }
}
