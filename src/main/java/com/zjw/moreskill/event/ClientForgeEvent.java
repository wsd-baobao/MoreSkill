package com.zjw.moreskill.event;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.entity.ModEntities;
import com.zjw.moreskill.entity.MoreSkillIronGolem;
import com.zjw.moreskill.network.MoreSkillIronGolemPacket;
import com.zjw.moreskill.network.NetworkHandler;
import com.zjw.moreskill.screen.SkillPanelScreen;
import com.zjw.moreskill.utils.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, value = Dist.CLIENT)
public class ClientForgeEvent {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.SKILL_PANEL_KEY.consumeClick()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                Minecraft.getInstance().setScreen(new SkillPanelScreen(player));
            }
        }

        if (KeyBindings.SUMMON_MORE_SKILL_IRON_GOLEM.consumeClick()) {
            MoreSkill.LOGGER.info("召唤铁傀儡");
            NetworkHandler.INSTANCE.sendToServer(new MoreSkillIronGolemPacket());
        }
    }

    

}
