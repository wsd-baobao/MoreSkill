package com.zjw.moreskill.event;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.entity.ModEntities;
import com.zjw.moreskill.entity.MoreSkillIronGolem;
import com.zjw.moreskill.utils.KeyBindings;

import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvent {
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        MoreSkill.LOGGER.info("注册属性");
        event.put(ModEntities.MORE_SKILL_IRON_GOLEM.get(), MoreSkillIronGolem.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 使用原版铁傀儡的渲染器来渲染你的自定义铁傀儡
        MoreSkill.LOGGER.info("注册渲染器");
        // IronGolemRenderer 是 Mojang 提供的类
        event.registerEntityRenderer(
                ModEntities.MORE_SKILL_IRON_GOLEM.get(),
                // 注意：这里传入 IronGolemRenderer 的构造函数引用
                // 它会自动处理模型和贴图
                IronGolemRenderer::new);
    }

    @SubscribeEvent
    public static void onKeyRegistry(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.SKILL_PANEL_KEY);
        event.register(KeyBindings.SUMMON_MORE_SKILL_IRON_GOLEM);
        event.register(KeyBindings.ATTRIBUTE_PANEL_KEY);
    }
}
