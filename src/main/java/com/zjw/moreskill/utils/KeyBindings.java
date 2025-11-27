package com.zjw.moreskill.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping SKILL_PANEL_KEY = new KeyMapping(
            "key.moreskill.open_ui", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "key.categories.moreskill");//打开面板

    public static final KeyMapping SUMMON_MORE_SKILL_IRON_GOLEM = new KeyMapping(
            "key.moreskill.summon.irongolem", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_T,
            "key.categories.moreskill");// 召唤铁傀儡

}
