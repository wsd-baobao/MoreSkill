package com.zjw.moreskill.event;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.screen.SkillPanelScreen;
import com.zjw.moreskill.utils.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


public class ClientEvent {

    @Mod.EventBusSubscriber(modid = MoreSkill.MODID,value = Dist.CLIENT)
    public static class ClientForgerEvent {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (KeyBindings.SKILL_PANEL_KEY.consumeClick()) {
                System.out.println("click");
                Minecraft instance = Minecraft.getInstance();
                Player player = instance.player;

                instance.setScreen(new SkillPanelScreen(player));
                // 打开自定义的菜单
//                if (player != null) {
//                    NetworkHooks.openScreen((ServerPlayer) player,new SkillPanelProvider());
//                }
            }

        }
        @SubscribeEvent
        public  static void onKeyRegistry(RegisterKeyMappingsEvent event){
            event.register(KeyBindings.SKILL_PANEL_KEY);
        }
    }
    // private record SkillPanelProvider() implements MenuProvider
    // {
    //     @Override
    //     public Component getDisplayName() {
    //         return Component.literal("container.moreskill.skill_panel");
    //     }

    //     @Override
    //     public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
    //         return new SkillPanelMenu(id, inventory, null);
    //     }
    // }


}
