package com.zjw.moreskill.datagen;

import com.zjw.moreskill.MoreSkill;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MoreSkillData {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new ModLanguageProvider(packOutput, MoreSkill.MODID, "en_us"));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, MoreSkill.MODID, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, MoreSkill.MODID, existingFileHelper));


        //server
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));
    }
}
