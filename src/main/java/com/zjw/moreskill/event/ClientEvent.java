package com.zjw.moreskill.event;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.screen.SkillPanelScreen;
import com.zjw.moreskill.utils.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvent {

    @SubscribeEvent
    public static void onKeyRegistry(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.SKILL_PANEL_KEY);
    }

    @Mod.EventBusSubscriber(modid = MoreSkill.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (KeyBindings.SKILL_PANEL_KEY.consumeClick()) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    Minecraft.getInstance().setScreen(new SkillPanelScreen(player));
                }
            }
        }
    }
}
