package com.zjw.moreskill.event;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.network.MoreSkillIronGolemPacket;
import com.zjw.moreskill.network.NetworkHandler;
import com.zjw.moreskill.network.SyncSkillRequestPacket;
import com.zjw.moreskill.screen.AttributePanelScreen;
import com.zjw.moreskill.screen.SkillPanelScreen;
import com.zjw.moreskill.utils.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, value = Dist.CLIENT)
public class ClientForgeEvent {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.SKILL_PANEL_KEY.consumeClick()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                // 向服务端请求最新的技能数据，避免面板显示过期数据
                NetworkHandler.INSTANCE.sendToServer(new SyncSkillRequestPacket());
                Minecraft.getInstance().setScreen(new SkillPanelScreen(player));
            }
        }

        if (KeyBindings.ATTRIBUTE_PANEL_KEY.consumeClick()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                Minecraft.getInstance().setScreen(new AttributePanelScreen(player));
            }
        }

        if (KeyBindings.SUMMON_MORE_SKILL_IRON_GOLEM.consumeClick()) {
            MoreSkill.LOGGER.info("召唤铁傀儡");
            NetworkHandler.INSTANCE.sendToServer(new MoreSkillIronGolemPacket());
        }
    }

    

}
