package com.zjw.moreskill.skill.fishing;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.*;

public class FishingPoolManager {
    private static final Map<Integer, List<Item>> ITEM_POOLS = new LinkedHashMap<>();
    private static final Map<Integer, ProbabilityFunction> POOL_PROBABILITY_FUNCTIONS = new LinkedHashMap<>();
    static {
        // 第 1 池：从 40% 降到 18%
        addItemPool(1, Arrays.asList(
                Items.COD, Items.SALMON,
                createEnchantedItemStack(Items.COD, 1)
        ), level -> {
            double min = 0.18;
            double max = 0.4;
            return Math.max(min, max - (max - min) * (level / 100.0));
        });

        // 第 2 池：从 29% 降到 18%
        addItemPool(11, Arrays.asList(
                Items.TROPICAL_FISH, Items.PUFFERFISH
        ), level -> {
            double min = 0.18;
            double max = 0.29;
            return Math.min(max, max - (max - min) * (level / 100.0));
        });

        // 第 3 池：从 13% 增到 15%
        addItemPool(21, Arrays.asList(
                Items.NAUTILUS_SHELL
        ), level -> {
            double min = 0.13;
            double max = 0.15;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });
        // 第 4 池：从 9% 增到 13%
        addItemPool(31, Arrays.asList(
                Items.COOKED_COD,
                Items.COOKED_SALMON
        ), level -> {
            double min = 0.09;
            double max = 0.13;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });
        // 第 5 池：从 6% 增到 11%
        addItemPool(41, Arrays.asList(
                Items.CLAY_BALL, Items.DIAMOND
        ), level -> {
            double min = 0.06;
            double max = 0.11;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });
        // 第 6 池：从 3% 增到 9%
        addItemPool(51, Arrays.asList(
                createEnchantedItemStack(Items.ENCHANTED_BOOK,30)

        ), level -> {
            double min = 0.03;
            double max = 0.09;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });

        // 第 7 池：从 0% 增到 7%
        addItemPool(61, Arrays.asList(
                Items.NAME_TAG
        ), level -> {
            double min = 0.0;
            double max = 0.07;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });

        // 第 8 池：从 0% 增到 5%
        addItemPool(71, Arrays.asList(
                Items.TRIDENT
        ), level -> {
            double min = 0.0;
            double max = 0.05;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });

        // 第 9 池：从 0% 增到 3%
        addItemPool(81, Arrays.asList(
                Items.GOLDEN_APPLE,
                Items.NETHERITE_BLOCK
        ), level -> {
            double min = 0.0;
            double max = 0.03;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });

        // 第 10 池：从 0% 增到 1%
        addItemPool(91, Arrays.asList(
                Items.TOTEM_OF_UNDYING,
                Items.ELYTRA,
                Items.DRAGON_EGG
        ), level -> {
            double min = 0.0;
            double max = 0.01;
            return Math.min(max, min + (max - min) * (level / 100.0));
        });

    }

    private static Item createEnchantedItemStack(Item item,int level) {
        RandomSource random = null;
        ItemStack stack = new ItemStack(item);
        if (Minecraft.getInstance().level != null) {
            random = Minecraft.getInstance().level.random;
            // 这里你可以根据需要调整附魔逻辑
            EnchantmentHelper.selectEnchantment(random, stack, level,true);
        }



        return stack.getItem();
    }

    @FunctionalInterface
    public interface ProbabilityFunction {
        double apply(int level);
    }

    private static void addItemPool(int minLevel, List<Item> items,ProbabilityFunction probabilityFunction) {
        ITEM_POOLS.put(minLevel, items);
        POOL_PROBABILITY_FUNCTIONS.put(minLevel, probabilityFunction);
    }

    public List<Item> getAvailableItems(int level) {
        List<Item> availableItems = new ArrayList<>();
        for (Map.Entry<Integer, List<Item>> entry : ITEM_POOLS.entrySet()) {
            if (level >= entry.getKey()) {
                availableItems.addAll(entry.getValue());
            }
        }
        return availableItems;
    }

    public List<Item> getRandomItems(int level, Random random, int numberOfItems) {
        List<Item> selectedItems = new ArrayList<>();
        for (int i = 0; i < numberOfItems; i++) {
            // 计算各池的权重
            Map<Integer, Double> poolWeights = new HashMap<>();
            double totalWeight = 0.0;

            for (Map.Entry<Integer, ProbabilityFunction> entry : POOL_PROBABILITY_FUNCTIONS.entrySet()) {
                if (level >= entry.getKey()) {
                    double weight = entry.getValue().apply(level);
                    if (weight > 0) {
                        poolWeights.put(entry.getKey(), weight);
                        totalWeight += weight;
                    }
                }
            }

            // 归一化后随机选择物品池
            double rand = random.nextDouble() * totalWeight;
            for (Map.Entry<Integer, Double> entry : poolWeights.entrySet()) {
                rand -= entry.getValue();
                if (rand <= 0) {
                    List<Item> poolItems = ITEM_POOLS.get(entry.getKey());
                    System.out.println(entry.getKey());
                    System.out.println(poolItems);
                    if (!poolItems.isEmpty()) {
                        selectedItems.add(poolItems.get(random.nextInt(poolItems.size())));
                    }
                    break;
                }
            }
        }
        return selectedItems;
    }

//    public List<Item> getRandomItems(int level, Random random, int numberOfItems) {
//        List<Item> selectedItems = new ArrayList<>();
//        for (int i = 0; i < numberOfItems; i++) {
//            for (Map.Entry<Integer, ProbabilityFunction> entry : POOL_PROBABILITY_FUNCTIONS.entrySet()) {
//                if (level >= entry.getKey() && entry.getValue().apply(level) > random.nextDouble()) {
//                    List<Item> poolItems = ITEM_POOLS.get(entry.getKey());
//                    if (!poolItems.isEmpty()) {
//                        selectedItems.add(poolItems.get(random.nextInt(poolItems.size())));
//                        break;
//                    }
//                }
//            }
//        }
//        return selectedItems;
//    }


    public static FishingPoolManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final FishingPoolManager INSTANCE = new FishingPoolManager();
    }
}
