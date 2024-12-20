package com.zjw.moreskill.utils;

import com.mojang.blaze3d.platform.InputConstants;
import com.zjw.moreskill.MoreSkill;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class KeyBindings {
    public static final KeyMapping SKILL_PANEL_KEY = new KeyMapping(
            "key.moreskill.open_ui", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "key.categories.moreskill");



}
