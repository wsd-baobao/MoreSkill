package com.zjw.moreskill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

//    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
//            .comment("Whether to log the dirt block on common setup")
//            .define("logDirtBlock", true);
//
//    private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER
//            .comment("A magic number")
//            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);
//
//    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
//            .comment("What you want the introduction message to be for the magic number")
//            .define("magicNumberIntroduction", "The magic number is... ");
//
//    // a list of strings that are treated as resource locations for items
//    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
//            .comment("A list of items to log on common setup.")
//            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);


    private static final ForgeConfigSpec.ConfigValue<List<List<String>>> FISHING_POOLS = BUILDER
            .comment("从0号池到9号池，越往后概率越小")
            .define("fishingPoolItems", defaultFishingPools(), Config::validateItemName);

    //添加矿物的配置
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> MINING_ORE = BUILDER
            .comment("添加矿物到到挖矿技能的列表")
            .define("addORE", List.of("minecraft:iron_ore", "minecraft:iron_ore"), Config::validateBlockName);

    private static List<List<String>> defaultFishingPools() {
        List<List<String>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i == 0) {
                list.add(Arrays.asList("minecraft:iron_ingot", "minecraft:gold_ingot"));
            } else {
                list.add(new ArrayList<>());
            }
        }
        return list;
    }

    static final ForgeConfigSpec SPEC = BUILDER.build();
//
//    public static boolean logDirtBlock;
//    public static int magicNumber;
//    public static String magicNumberIntroduction;
//    public static Set<Item> items;
    public static List<List<Item>> fishingPools;
    public static List<Block> miningOre;

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }
    private static boolean validateBlockName(final Object obj) {
        return obj instanceof final String blockName && ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(blockName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
//        logDirtBlock = LOG_DIRT_BLOCK.get();
//        magicNumber = MAGIC_NUMBER.get();
//        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
        miningOre = MINING_ORE.get().stream()
                .map(blockName -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName)))
                .collect(Collectors.toList());

        fishingPools = FISHING_POOLS.get().stream()
                .map(list -> list.stream()
                        .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        // convert the list of strings into a set of items
//        items = ITEM_STRINGS.get().stream()
//                .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
//                .collect(Collectors.toSet());
    }
}
