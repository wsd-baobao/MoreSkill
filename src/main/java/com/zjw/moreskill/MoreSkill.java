package com.zjw.moreskill;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.zjw.moreskill.attribute.AttributeEffectHandler;
import com.zjw.moreskill.entity.ModEntities;
import com.zjw.moreskill.item.ModItems;
import com.zjw.moreskill.network.NetworkHandler;
import com.zjw.moreskill.skill.CapabilityEventHandler;
import com.zjw.moreskill.skill.alchemy.AlchemyHandler;
import com.zjw.moreskill.skill.combat.CombatHandler;
import com.zjw.moreskill.skill.cooking.CookingHandler;
import com.zjw.moreskill.skill.farming.FarmingHandler;
import com.zjw.moreskill.skill.fishing.FishingHandler;
import com.zjw.moreskill.skill.mining.MiningHandler;
import com.zjw.moreskill.skill.smithing.SmithingHandler;
import com.zjw.moreskill.skill.trading.TradingHandler;
import com.zjw.moreskill.skill.woodcutting.WoodCuttingHandler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MoreSkill.MODID)
public class MoreSkill {

    public static final String MODID = "moreskill";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MoreSkill() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册事件处理器实例（实例方法必须通过 register(instance) 注册，
        // @Mod.EventBusSubscriber 自动注册仅支持静态方法）
        MinecraftForge.EVENT_BUS.register(new FishingHandler());
        MinecraftForge.EVENT_BUS.register(new MiningHandler());
        MinecraftForge.EVENT_BUS.register(new SmithingHandler());
        MinecraftForge.EVENT_BUS.register(new FarmingHandler());
        MinecraftForge.EVENT_BUS.register(new CookingHandler());
        MinecraftForge.EVENT_BUS.register(new CombatHandler());
        MinecraftForge.EVENT_BUS.register(new AlchemyHandler());
        MinecraftForge.EVENT_BUS.register(new TradingHandler());
        MinecraftForge.EVENT_BUS.register(new WoodCuttingHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityEventHandler());
        MinecraftForge.EVENT_BUS.register(new AttributeEffectHandler());

        MoreSkillTab.CREATIVE_MODE_TABS.register(modEventBus);
        NetworkHandler.register();
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);

        

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
