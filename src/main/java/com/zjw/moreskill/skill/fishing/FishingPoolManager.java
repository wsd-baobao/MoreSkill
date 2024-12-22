package com.zjw.moreskill.skill.fishing;

import com.zjw.moreskill.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.*;

//todo 添加从配置获取物品池的功能

public class FishingPoolManager {
    private static final Map<Integer, List<ItemStack>> ITEM_POOLS = new LinkedHashMap<>();
    private static final Map<Integer, ProbabilityFunction> POOL_PROBABILITY_FUNCTIONS = new LinkedHashMap<>();

    private static Map<Integer, Double> cachedPoolWeights = new HashMap<>();
    private static int lastCalculatedLevel = -1;

    @FunctionalInterface
    public interface ProbabilityFunction {
        double apply(int level);
    }
    private static ItemStack createEnchantedItemStack(Item item, int level) {
        RandomSource random = null;
        ItemStack stack = new ItemStack(item);
        if (Minecraft.getInstance().level != null) {
            random = Minecraft.getInstance().level.random;
            // 这里你可以根据需要调整附魔逻辑
            List<EnchantmentInstance> enchantmentInstances = EnchantmentHelper.selectEnchantment(random, stack, level, true);

            if (item == Items.BOOK) {
                ItemStack enchantBook = new ItemStack(Items.ENCHANTED_BOOK);
                for (EnchantmentInstance enchantmentInstance : enchantmentInstances) {
                    EnchantedBookItem.addEnchantment(enchantBook, enchantmentInstance);
                }
                return enchantBook;
            } else {
                for (EnchantmentInstance enchantmentInstance : enchantmentInstances) {
                    stack.enchant(enchantmentInstance.enchantment, enchantmentInstance.level);
                }
            }
        }
        return stack;
    }

    private static void addItemPool(int minLevel, List<ItemStack> items, ProbabilityFunction probabilityFunction) {
        ITEM_POOLS.put(minLevel, items);
        POOL_PROBABILITY_FUNCTIONS.put(minLevel, probabilityFunction);
    }
    public List<ItemStack> getRandomItems(int level, Random random, int numberOfItems) {

        if (level != lastCalculatedLevel){
            cachedPoolWeights.clear();
            for (Map.Entry<Integer, ProbabilityFunction> entry : POOL_PROBABILITY_FUNCTIONS.entrySet()) {
                if (level >= entry.getKey()) {
                    double weight = entry.getValue().apply(level);
                    if (weight > 0) {
                        cachedPoolWeights.put(entry.getKey(), weight);
                    }
                }
            }
        }
        List<ItemStack> selectedItems = new ArrayList<>();
        for (int i = 0; i < numberOfItems; i++) {
            // 计算各池的权重
            double totalWeight = cachedPoolWeights.values().stream().mapToDouble(Double::doubleValue).sum();
            // 归一化后随机选择物品池
            double rand = random.nextDouble() * totalWeight;
            for (Map.Entry<Integer, Double> entry : cachedPoolWeights.entrySet()) {
                rand -= entry.getValue();
                if (rand <= 0) {
                    List<ItemStack> poolItems = ITEM_POOLS.get(entry.getKey());
//                    System.out.println(entry.getKey());
//                    System.out.println(poolItems);
                    if (!poolItems.isEmpty()) {
                        selectedItems.add(poolItems.get(random.nextInt(poolItems.size())));
                    }
                    break;
                }
            }
        }
        return selectedItems;
    }
    private static void addItemPoolForConfig() {
        List<List<Item>> fishingPools = Config.fishingPools;
        for (int i = 0; i < fishingPools.size(); i++){
            List<Item> pool = fishingPools.get(i);
            if (!pool.isEmpty()){
                for (Item item : pool) {
                    ITEM_POOLS.get(i*10).add(new ItemStack(item));
//                    System.out.println("添加配置中的物品");
//                    System.out.println(item);
                }

            }
        }

    }

    static {
        // 第 1 池：从 40% 降到 18%
        addItemPool(0, new FishingPoolBuilder().addItems(
                        Items.COD,
                        Items.SALMON,
                        Items.TROPICAL_FISH,
                        Items.PUFFERFISH,
                        Items.ROTTEN_FLESH,
                        Items.SPIDER_EYE,
                        Items.SWEET_BERRIES,
                        Items.BEETROOT,
                        Items.COOKIE,
                        Items.POTION,
                        Items.TORCH,
                        Items.LANTERN,
                        Items.FLOWER_POT,
                        Items.STRING,
                        Items.BONE_MEAL,
                        Items.STICK,
                        Items.CLAY_BALL,
                        Items.GLASS_BOTTLE,
                        Items.IRON_NUGGET,
                        Items.SNOWBALL,
                        Items.FISHING_ROD
                ).addAllItemStacks(
                        Arrays.asList(
                                createEnchantedItemStack(Items.BOOK, 1),
                                createEnchantedItemStack(Items.BOOK, 1),
                                createEnchantedItemStack(Items.BOOK, 1),
                                createEnchantedItemStack(Items.BOOK, 1),
                                createEnchantedItemStack(Items.BOOK, 1),
                                createEnchantedItemStack(Items.BOOK, 1),
                                createEnchantedItemStack(Items.FISHING_ROD, 5),
                                createEnchantedItemStack(Items.FISHING_ROD, 5),
                                createEnchantedItemStack(Items.FISHING_ROD, 5)
                        )
                ).build()
                , level -> {
                    double min = 0.18;
                    double max = 0.4;
                    return Math.max(min, max - (max - min) * (level / 100.0));
                });

        // 第 2 池：从 29% 降到 18%
        addItemPool(10, new FishingPoolBuilder().addItems(
                Items.COD,
                Items.SALMON,
                Items.TROPICAL_FISH,
                Items.PUFFERFISH,
                Items.APPLE,
                Items.MELON_SLICE,
                Items.GLOW_BERRIES,
                Items.CARROT,
                Items.POTATO,
                Items.POISONOUS_POTATO,
                Items.MUSHROOM_STEW,
                Items.BEETROOT_SOUP,
                Items.RABBIT_STEW,
                Items.EGG,
                Items.ARROW,
                Items.BOW,
                Items.CROSSBOW,
                Items.LEATHER,
                Items.RABBIT_HIDE,
                Items.INK_SAC,
                Items.GLOW_INK_SAC,
                Items.SLIME_BALL,
                Items.CHARCOAL,
                Items.COAL,
                Items.CLAY_BALL,
                Items.GLASS_BOTTLE,
                Items.SHIELD,
                Items.LEATHER_BOOTS,
                Items.LEATHER_HELMET,
                Items.LEATHER_LEGGINGS,
                Items.LEATHER_CHESTPLATE,
                Items.NAME_TAG
        ).addAllItemStacks(
                Arrays.asList(
                        createEnchantedItemStack(Items.STONE_PICKAXE, 5),
                        createEnchantedItemStack(Items.STONE_PICKAXE, 5),
                        createEnchantedItemStack(Items.STONE_PICKAXE, 5),
                        createEnchantedItemStack(Items.STONE_AXE, 5),
                        createEnchantedItemStack(Items.STONE_AXE, 5),
                        createEnchantedItemStack(Items.STONE_AXE, 5),
                        createEnchantedItemStack(Items.STONE_SWORD, 5),
                        createEnchantedItemStack(Items.STONE_SWORD, 5),
                        createEnchantedItemStack(Items.STONE_SWORD, 5),
                        createEnchantedItemStack(Items.BOOK, 5),
                        createEnchantedItemStack(Items.BOOK, 5),
                        createEnchantedItemStack(Items.BOOK, 5),
                        createEnchantedItemStack(Items.BOOK, 5)
                )
        ).build(), level -> {
            double min = 0.18;
            double max = 0.29;
            return Math.min(max, max - (max - min) * (level / 100.0));
        });
        // 第 3 池：从 13% 增到 15%
        addItemPool(20, new FishingPoolBuilder().addItems(
                Items.NAUTILUS_SHELL,
                Items.COAL,
                Items.RAW_IRON,
                Items.RAW_COPPER,
                Items.LAPIS_LAZULI,
                Items.BONE,
                Items.BONE_BLOCK,
                Items.FEATHER,
                Items.IRON_INGOT,
                Items.COPPER_INGOT,
                Items.QUARTZ,
                Items.REDSTONE,
                Items.GLOWSTONE_DUST,
                Items.GUNPOWDER,
                Items.BLAZE_POWDER,
                Items.SUGAR,
                Items.GLISTERING_MELON_SLICE,
                Items.NETHER_WART,
                Items.PHANTOM_MEMBRANE,
                Items.FIRE_CHARGE,
                Items.BLAZE_ROD,
                Items.PRISMARINE_SHARD,
                Items.PRISMARINE_CRYSTALS,
                Items.ECHO_SHARD,
                Items.GOLDEN_APPLE,
                Items.NAME_TAG,
                Items.HONEY_BOTTLE,
                Items.MILK_BUCKET
        ).addAllItemStacks(
                Arrays.asList(
                        createEnchantedItemStack(Items.BOOK, 10),
                        createEnchantedItemStack(Items.BOOK, 10),
                        createEnchantedItemStack(Items.BOOK, 10),
                        createEnchantedItemStack(Items.BOOK, 10),
                        createEnchantedItemStack(Items.BOOK, 10)
                )
        ).build(), level -> {
            double min = 0.13;
            double max = 0.15;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });
        // 第 4 池：从 9% 增到 13%
        addItemPool(30, new FishingPoolBuilder().addItems(
                Items.BLAZE_ROD,
                Items.PRISMARINE_SHARD,
                Items.PRISMARINE_CRYSTALS,
                Items.ECHO_SHARD,
                Items.COAL,
                Items.RAW_IRON,
                Items.RAW_COPPER,
                Items.LAPIS_LAZULI,
                Items.IRON_INGOT,
                Items.COPPER_INGOT,
                Items.RAW_GOLD,
                Items.GOLD_INGOT,
                Items.EMERALD,
                Items.DIAMOND,
                Items.ANCIENT_DEBRIS,
                Items.EXPERIENCE_BOTTLE,
                Items.TRIPWIRE_HOOK,
                Items.TNT,
                Items.OBSIDIAN,
                Items.SLIME_BLOCK,
                Items.ENDER_PEARL
//                Items.,
        ).addAllItemStacks(
                Arrays.asList(
                        createEnchantedItemStack(Items.IRON_SWORD, 10),
                        createEnchantedItemStack(Items.IRON_SWORD, 10),
                        createEnchantedItemStack(Items.IRON_SWORD, 10),
                        createEnchantedItemStack(Items.IRON_AXE, 10),
                        createEnchantedItemStack(Items.IRON_AXE, 10),
                        createEnchantedItemStack(Items.IRON_AXE, 10),
                        createEnchantedItemStack(Items.IRON_PICKAXE, 10),
                        createEnchantedItemStack(Items.IRON_PICKAXE, 10),
                        createEnchantedItemStack(Items.IRON_PICKAXE, 10),
                        createEnchantedItemStack(Items.IRON_HELMET, 10),
                        createEnchantedItemStack(Items.IRON_HELMET, 10),
                        createEnchantedItemStack(Items.IRON_HELMET, 10),
                        createEnchantedItemStack(Items.IRON_CHESTPLATE, 10),
                        createEnchantedItemStack(Items.IRON_CHESTPLATE, 10),
                        createEnchantedItemStack(Items.IRON_CHESTPLATE, 10),
                        createEnchantedItemStack(Items.IRON_LEGGINGS, 10),
                        createEnchantedItemStack(Items.IRON_LEGGINGS, 10),
                        createEnchantedItemStack(Items.IRON_LEGGINGS, 10),
                        createEnchantedItemStack(Items.IRON_BOOTS, 10),
                        createEnchantedItemStack(Items.IRON_BOOTS, 10),
                        createEnchantedItemStack(Items.IRON_BOOTS, 10),
                        createEnchantedItemStack(Items.FISHING_ROD, 15)
                )
        ).build(), level -> {
            double min = 0.09;
            double max = 0.13;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });
        // 第 5 池：从 6% 增到 11%
        addItemPool(40, new FishingPoolBuilder().addItems(
                Items.DIAMOND,
                Items.DIAMOND,
                Items.COAL,
                Items.RAW_IRON,
                Items.RAW_COPPER,
                Items.LAPIS_LAZULI,
                Items.IRON_INGOT,
                Items.COPPER_INGOT,
                Items.RAW_GOLD,
                Items.GOLD_INGOT,
                Items.GOLD_INGOT,
                Items.EMERALD,
                Items.EMERALD,
                Items.DIAMOND,
                Items.DIAMOND,
                Items.DIAMOND_BLOCK,
                Items.EMERALD_BLOCK,
                Items.IRON_BLOCK,
                Items.GOLD_BLOCK,
                Items.LAPIS_BLOCK,
                Items.REDSTONE_BLOCK,
                Items.QUARTZ,
                Items.QUARTZ_BLOCK,
                Items.COPPER_BLOCK,
                Items.COAL_BLOCK,
                Items.COAL_BLOCK,
                Items.COAL_BLOCK,
                Items.OBSIDIAN,
                Items.AMETHYST_SHARD,
                Items.AMETHYST_BLOCK,
                Items.GOLDEN_APPLE,
                Items.ENDER_PEARL
        ).build(), level -> {
            double min = 0.06;
            double max = 0.11;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });
        // 第 6 池：从 3% 增到 9%
        addItemPool(50, new FishingPoolBuilder().addItems(
                Items.DIAMOND_AXE,
                Items.DIAMOND_SWORD,
                Items.DIAMOND_PICKAXE,
                Items.DIAMOND_HOE,
                Items.DIAMOND_SHOVEL,
                Items.DIAMOND_HELMET,
                Items.DIAMOND_CHESTPLATE,
                Items.DIAMOND_LEGGINGS,
                Items.DIAMOND_BOOTS,
                Items.TRIDENT,
                Items.DIAMOND,
                Items.DIAMOND,
                Items.COAL,
                Items.RAW_IRON,
                Items.RAW_COPPER,
                Items.LAPIS_LAZULI,
                Items.IRON_INGOT,
                Items.COPPER_INGOT,
                Items.RAW_GOLD,
                Items.GOLD_INGOT,
                Items.GOLD_INGOT,
                Items.EMERALD,
                Items.EMERALD,
                Items.DIAMOND,
                Items.DIAMOND,
                Items.DIAMOND_BLOCK,
                Items.EMERALD_BLOCK,
                Items.IRON_BLOCK,
                Items.GOLD_BLOCK,
                Items.LAPIS_BLOCK,
                Items.REDSTONE_BLOCK,
                Items.QUARTZ,
                Items.QUARTZ_BLOCK,
                Items.COPPER_BLOCK,
                Items.COAL_BLOCK,
                Items.COAL_BLOCK,
                Items.COAL_BLOCK,
                Items.OBSIDIAN,
                Items.GOLDEN_APPLE,
                Items.AMETHYST_SHARD,
                Items.SHULKER_SHELL,
                Items.AMETHYST_BLOCK
        ).addAllItemStacks(
                Arrays.asList(
                        createEnchantedItemStack(Items.BOOK, 15),
                        createEnchantedItemStack(Items.BOOK, 15),
                        createEnchantedItemStack(Items.BOOK, 15),
                        createEnchantedItemStack(Items.BOOK, 15),
                        createEnchantedItemStack(Items.BOOK, 15),
                        createEnchantedItemStack(Items.BOOK, 15),
                        createEnchantedItemStack(Items.DIAMOND_AXE, 15),
                        createEnchantedItemStack(Items.DIAMOND_AXE, 15),
                        createEnchantedItemStack(Items.DIAMOND_AXE, 15),
                        createEnchantedItemStack(Items.DIAMOND_PICKAXE, 15),
                        createEnchantedItemStack(Items.DIAMOND_PICKAXE, 15),
                        createEnchantedItemStack(Items.DIAMOND_PICKAXE, 15),
                        createEnchantedItemStack(Items.DIAMOND_SWORD, 15),
                        createEnchantedItemStack(Items.DIAMOND_SWORD, 15),
                        createEnchantedItemStack(Items.DIAMOND_SWORD, 15),
                        createEnchantedItemStack(Items.TRIDENT, 15),
                        createEnchantedItemStack(Items.TRIDENT, 15),
                        createEnchantedItemStack(Items.TRIDENT, 15),
                        createEnchantedItemStack(Items.FISHING_ROD, 15)
                )
        ).build(), level -> {
            double min = 0.03;
            double max = 0.09;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });

        // 第 7 池：从 0% 增到 7%
        addItemPool(60, new FishingPoolBuilder().addItems(
                Items.NAME_TAG,
                Items.NETHER_STAR,
                Items.DIAMOND_BLOCK,
                Items.DIAMOND_BLOCK,
                Items.DIAMOND_BLOCK,
                Items.EMERALD_BLOCK,
                Items.EMERALD_BLOCK,
                Items.IRON_BLOCK,
                Items.IRON_BLOCK,
                Items.GOLD_BLOCK,
                Items.LAPIS_BLOCK,
                Items.REDSTONE_BLOCK,
                Items.QUARTZ_BLOCK,
                Items.COPPER_BLOCK,
                Items.COAL_BLOCK,
                Items.SHULKER_SHELL,
                Items.NETHERITE_INGOT
        ).addAllItemStacks(
                Arrays.asList(
                        createEnchantedItemStack(Items.BOOK, 30),
                        createEnchantedItemStack(Items.BOOK, 30),
                        createEnchantedItemStack(Items.BOOK, 30),
                        createEnchantedItemStack(Items.BOOK, 30),
                        createEnchantedItemStack(Items.BOOK, 30),
                        createEnchantedItemStack(Items.BOOK, 30),
                        createEnchantedItemStack(Items.DIAMOND_HELMET, 30),
                        createEnchantedItemStack(Items.DIAMOND_HELMET, 30),
                        createEnchantedItemStack(Items.DIAMOND_HELMET, 30),
                        createEnchantedItemStack(Items.DIAMOND_CHESTPLATE, 30),
                        createEnchantedItemStack(Items.DIAMOND_CHESTPLATE, 30),
                        createEnchantedItemStack(Items.DIAMOND_CHESTPLATE, 30),
                        createEnchantedItemStack(Items.DIAMOND_LEGGINGS, 30),
                        createEnchantedItemStack(Items.DIAMOND_LEGGINGS, 30),
                        createEnchantedItemStack(Items.DIAMOND_LEGGINGS, 30),
                        createEnchantedItemStack(Items.DIAMOND_BOOTS, 30),
                        createEnchantedItemStack(Items.DIAMOND_BOOTS, 30),
                        createEnchantedItemStack(Items.DIAMOND_BOOTS, 30),
                        createEnchantedItemStack(Items.FISHING_ROD, 30)
                )
        ).build(), level -> {
            double min = 0.0;
            double max = 0.07;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });
        // 第 8 池：从 0% 增到 5%
        addItemPool(70, new FishingPoolBuilder().addItems(
                Items.TRIDENT,
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHER_STAR,
                Items.SHULKER_SHELL,
                Items.DIAMOND_BLOCK,
                Items.EMERALD_BLOCK,
                Items.IRON_BLOCK,
                Items.GOLD_BLOCK,
                Items.LAPIS_BLOCK,
                Items.REDSTONE_BLOCK,
                Items.QUARTZ_BLOCK,
                Items.COPPER_BLOCK,
                Items.COAL_BLOCK,
                Items.DIAMOND_BLOCK,
                Items.EMERALD_BLOCK,
                Items.IRON_BLOCK,
                Items.GOLD_BLOCK,
                Items.LAPIS_BLOCK,
                Items.REDSTONE_BLOCK,
                Items.QUARTZ_BLOCK,
                Items.COPPER_BLOCK
        ).addAllItemStacks(
                Arrays.asList(
                        createEnchantedItemStack(Items.TRIDENT, 30),
                        createEnchantedItemStack(Items.TRIDENT, 30),
                        createEnchantedItemStack(Items.TRIDENT, 30),
                        createEnchantedItemStack(Items.FISHING_ROD, 30)
                )
        ).build(), level -> {
            double min = 0.0;
            double max = 0.05;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });

        // 第 9 池：从 0% 增到 3%
        addItemPool(80, new FishingPoolBuilder().addItems(
                Items.GOLDEN_APPLE,
                Items.ENCHANTED_GOLDEN_APPLE,
                Items.NETHERITE_BLOCK,
                Items.NETHERITE_BLOCK,
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHER_STAR,
                Items.NETHERITE_AXE,
                Items.NETHERITE_PICKAXE,
                Items.NETHERITE_SWORD,
                Items.NETHERITE_HELMET,
                Items.NETHERITE_CHESTPLATE,
                Items.NETHERITE_LEGGINGS,
                Items.NETHERITE_BOOTS
        ).addAllItemStacks(
                Arrays.asList(
                        createEnchantedItemStack(Items.NETHERITE_AXE, 30),
                        createEnchantedItemStack(Items.NETHERITE_AXE, 30),
                        createEnchantedItemStack(Items.NETHERITE_PICKAXE, 30),
                        createEnchantedItemStack(Items.NETHERITE_PICKAXE, 30),
                        createEnchantedItemStack(Items.NETHERITE_SWORD, 30),
                        createEnchantedItemStack(Items.NETHERITE_SWORD, 30),
                        createEnchantedItemStack(Items.NETHERITE_HELMET, 30),
                        createEnchantedItemStack(Items.NETHERITE_HELMET, 30),
                        createEnchantedItemStack(Items.NETHERITE_CHESTPLATE, 30),
                        createEnchantedItemStack(Items.NETHERITE_CHESTPLATE, 30),
                        createEnchantedItemStack(Items.NETHERITE_LEGGINGS, 30),
                        createEnchantedItemStack(Items.NETHERITE_LEGGINGS, 30),
                        createEnchantedItemStack(Items.NETHERITE_BOOTS, 30),
                        createEnchantedItemStack(Items.NETHERITE_BOOTS, 30)
                )
        ).build(), level -> {
            double min = 0.0;
            double max = 0.03;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });

        // 第 10 池：从 0% 增到 1%
        addItemPool(90, new FishingPoolBuilder().addItems(
                Items.TOTEM_OF_UNDYING,
                Items.ELYTRA,
                Items.DRAGON_EGG
        ).build(), level -> {
            double min = 0.0;
            double max = 0.01;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });

        addItemPoolForConfig();
    }


}
