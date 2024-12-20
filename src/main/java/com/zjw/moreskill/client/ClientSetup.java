package com.zjw.moreskill.client;

import com.zjw.moreskill.menu.ModMenus;
import com.zjw.moreskill.screen.SkillPanelScreen;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientSetup {
     public static void initEarly() {
        // run on mod construction
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
    }

    static void init(FMLClientSetupEvent event) {
        event.enqueueWork(ClientSetup::initLate);
    }

    private static void initLate() {
        // stuff that needs doing on the main thread
        registerScreenFactories();
    }

    private static void registerScreenFactories() {
//        MenuScreens.register(ModMenus.SKILL_PANEL.get(), SkillPanelScreen::new);
    }
}
