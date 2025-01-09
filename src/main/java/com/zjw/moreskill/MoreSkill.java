package com.zjw.moreskill;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(MoreSkill.MODID)
public class MoreSkill {
    
    public static final String MODID = "moreskill";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MoreSkill() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

//        MinecraftForge.EVENT_BUS.register(this);
//        aaa.RECIPE_SERIALIZERS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new FishingHandler());
        MinecraftForge.EVENT_BUS.register(new MiningHandler());
        MinecraftForge.EVENT_BUS.register(new SmithingHandler());
        MinecraftForge.EVENT_BUS.register(new FarmingHandler());
        MinecraftForge.EVENT_BUS.register(new CookingHandler());
        MinecraftForge.EVENT_BUS.register(new CombatHandler());
        MinecraftForge.EVENT_BUS.register(new AlchemyHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityEventHandler());
        MoreSkillTab.CREATIVE_MODE_TABS.register(modEventBus);
        NetworkHandler.register();
        ModItems.ITEMS.register(modEventBus);

        modEventBus.addListener(this::addCreative);


        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
