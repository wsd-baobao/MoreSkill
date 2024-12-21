package com.zjw.moreskill.skill;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.skill.fishing.FishingSkillProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//处理能力的加载与保存

@Mod.EventBusSubscriber(modid = MoreSkill.MODID,bus=Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityEventHandler {
    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(MoreSkill.MODID, "fishing_skill"), new FishingSkillProvider());
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        System.out.println("onPlayerLoggedIn");
        event.getEntity().getCapability(FishingSkillProvider.FISHING_SKILL).ifPresent(skill -> {
//            System.out.println("获取Skill");
//            System.out.println(skill);
            skill.deserializeNBT(event.getEntity().getPersistentData().getCompound("fishing_skill"));
        });

    }
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        event.getEntity().getCapability(FishingSkillProvider.FISHING_SKILL).ifPresent(skill -> {
//            System.out.println("保存Skill");
//            System.out.println(skill);
            CompoundTag compoundTag = skill.serializeNBT();
            event.getEntity().getPersistentData().put("fishing_skill", compoundTag);
        });

    }
}
