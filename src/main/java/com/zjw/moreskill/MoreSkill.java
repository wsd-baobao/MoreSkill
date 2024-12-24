package com.zjw.moreskill;

import com.zjw.moreskill.skill.CapabilityEventHandler;
import com.zjw.moreskill.skill.fishing.FishingHandler;
import com.zjw.moreskill.skill.mining.MiningHandler;
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

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MoreSkill.MODID)
public class MoreSkill {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "moreskill";
    
    public MoreSkill() {


        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

       
        modEventBus.addListener(this::commonSetup);


        MinecraftForge.EVENT_BUS.register(this);
        
        MinecraftForge.EVENT_BUS.register(new FishingHandler());
        MinecraftForge.EVENT_BUS.register(new MiningHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityEventHandler());
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
