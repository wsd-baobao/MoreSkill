package com.zjw.moreskill.skill.farming;

import com.zjw.moreskill.MoreSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FarmingHandler {
    private final Random random = new Random();

    // Base experience values
    private final int BASE_HARVEST_EXP = 5; // Basic harvesting experience
    private final int BASE_PLANT_EXP = 2; // Basic planting experience
    private final int BASE_BONEMEAL_EXP = 1; // Basic bonemeal experience

    // Skill effect multipliers
    private final float DROP_MULTIPLIER_PER_LEVEL = 0.02f; // 2% additional drop rate per level
    private final float DROP_CHANCE_PER_LEVEL = 0.5f; // 0.5% trigger chance increase per level
    private final float BONEMEAL_SAVE_CHANCE_PER_LEVEL = 0.4f; // 0.4% bonemeal saving chance per level

    // Player growth timer mappings
    private final Map<UUID, Integer> playerGrowthTimers = new HashMap<>();
    private final Map<UUID, BlockPos> playerLastPositions = new HashMap<>();

    /**
     * Check if a block is harvestable
     */
    private boolean isHarvestable(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CropBlock crop) {
            return crop.isMaxAge(state);
        } else if (block instanceof MelonBlock || block instanceof PumpkinBlock) {
            return true;
        }
        return false;
    }

    /**
     * Check if a block is a crop
     */
    private boolean isCrop(Block block) {
        boolean isCropBlock = block instanceof CropBlock ||
                block instanceof StemBlock ||
                block.getDescriptionId().toLowerCase().contains("crop") ||
                block.getDescriptionId().toLowerCase().contains("plant");
        
        return isCropBlock;
    }

    /**
     * Attempt to boost crop growth for a player
     */
    private void tryGrowthBoost(ServerPlayer player, int level, int radius) {
        // Get the player's world and position
        ServerLevel world = player.serverLevel();
        BlockPos playerPos = player.blockPosition();

        // Calculate the growth boost amount
        int growthBoost = Math.min(1 + level / 25, 3);

        // Initialize variables to track growth
        boolean didGrow = false;
        int totalBlocksChecked = 0;
        int cropBlocksFound = 0;

        // Check blocks in a radius around the player
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Check blocks above and below the player
                for (int y = -1; y <= 1; y++) {
                    // Get the target block position
                    BlockPos targetPos = playerPos.offset(x, y, z);

                    // Skip if the chunk is not loaded
                    if (!world.hasChunkAt(targetPos)) {
                        continue;
                    }

                    totalBlocksChecked++;

                    // Get the target block state
                    BlockState targetState = world.getBlockState(targetPos);
                    Block targetBlock = targetState.getBlock();

                    // Check if the block is a crop
                    if (isCrop(targetBlock)) {
                        cropBlocksFound++;

                        // Check if the crop is not fully grown
                        if (targetBlock instanceof CropBlock cropBlock) {
                            int currentAge = cropBlock.getAge(targetState);
                            int maxAge = cropBlock.getMaxAge();

                            // Grow the crop
                            if (currentAge < maxAge) {
                                int newAge = Math.min(maxAge, currentAge + growthBoost);
                                BlockState newState = cropBlock.getStateForAge(newAge);
                                world.setBlock(targetPos, newState, Block.UPDATE_ALL);
                                world.levelEvent(2005, targetPos, 0); // Play a growth sound
                                MoreSkill.LOGGER.info("Grew crop {} at {} from age {} to {}", targetBlock.getDescriptionId(), targetPos, currentAge, newAge);
                                didGrow = true;
                            }
                        } else if (targetBlock instanceof StemBlock) {
                            IntegerProperty ageProperty = StemBlock.AGE;
                            int currentAge = targetState.getValue(ageProperty);
                            int maxAge = 7;

                            // Grow the stem
                            if (currentAge < maxAge) {
                                int newAge = Math.min(maxAge, currentAge + growthBoost);
                                world.setBlock(targetPos, targetState.setValue(ageProperty, newAge), Block.UPDATE_ALL);
                                world.levelEvent(2005, targetPos, 0); // Play a growth sound
                                MoreSkill.LOGGER.info("Grew stem block {} at {} from age {} to {}", targetBlock.getDescriptionId(), targetPos, currentAge, newAge);
                                didGrow = true;
                            }
                        }
                    }
                }
            }
        }

        // Display a message to the player if growth occurred
        if (didGrow) {
            player.displayClientMessage(
                    Component.literal("Grew crops in a radius of " + radius + " blocks (+" + growthBoost + " growth)")
                            .withStyle(ChatFormatting.GREEN),
                    true);
        } else {
            MoreSkill.LOGGER.info(
                    "No crops found to grow. Total blocks checked: {}, Crop blocks found: {}, Radius: {}, Player: {}",
                    totalBlocksChecked, cropBlocksFound, radius, player.getName().getString());
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide
                && event.player instanceof ServerPlayer player) {
            player.getCapability(FarmingProvider.FARMING_CAPABILITY).ifPresent(farming -> {
                UUID playerId = player.getUUID();
                int level = farming.getLevel();

                BlockPos currentPos = player.blockPosition();
                playerLastPositions.put(playerId, currentPos);

                // Calculate frequency and range based on level
                int frequency = Math.max(120, 600 - level * 8); // Minimum 2 seconds (40 ticks), maximum 6 seconds (120 ticks)
                int radius = Math.min(1 + level / 10, 10); // Minimum radius 1, maximum radius 10

                // Update timer
                int timer = playerGrowthTimers.getOrDefault(playerId, random.nextInt(frequency) + frequency);
                timer--;

                if (timer <= 0) {
                    // Trigger growth boost
                    MoreSkill.LOGGER.info("Attempting growth boost for player {} with level {} and radius {}", player.getName().getString(), level, radius);
                    tryGrowthBoost(player, level, radius);
                    // Reset timer
                    timer = random.nextInt(frequency) + frequency;
                }

                playerGrowthTimers.put(playerId, timer);
            });
        }
    }

    @SubscribeEvent
    public void onCropHarvest(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        BlockState state = event.getState();

        // Check if the block is harvestable
        if (isHarvestable(state)) {
            // Get the player's farming capability
            player.getCapability(FarmingProvider.FARMING_CAPABILITY).ifPresent(farming -> {
                int level = farming.getLevel();

                // Add experience for harvesting
                farming.addExp(BASE_HARVEST_EXP);

                // Calculate drop chance and extra drops
                float dropChance = level * DROP_CHANCE_PER_LEVEL;
                if (random.nextFloat() * 100 < dropChance) {
                    // Calculate extra drops
                    float extraMultiplier = 1 + (level * DROP_MULTIPLIER_PER_LEVEL);
                    int extraDrops = Math.min(3, Math.round(extraMultiplier));

                    // Drop extra items
                    for (int i = 0; i < extraDrops; i++) {
                        Block.dropResources(state, player.level(), event.getPos(), null, player,
                                player.getMainHandItem());
                    }

                    // Display a message to the player
                    player.displayClientMessage(
                            Component.literal("Harvested " + (extraDrops + 1) + " items")
                                    .withStyle(ChatFormatting.GREEN),
                            true);
                }

                // Display a message to the player
                player.displayClientMessage(
                        Component.literal("Gained " + BASE_HARVEST_EXP + " experience (level " + level + ")")
                                .withStyle(ChatFormatting.GREEN),
                        true);
            });
        }
    }

    @SubscribeEvent
    public void onCropPlant(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Block block = event.getPlacedBlock().getBlock();
        if (isCrop(block)) {
            // Get the player's farming capability
            player.getCapability(FarmingProvider.FARMING_CAPABILITY).ifPresent(farming -> {
                int level = farming.getLevel();

                // Add experience for planting
                farming.addExp(BASE_PLANT_EXP);

                // Display a message to the player
                player.displayClientMessage(
                        Component.literal("Gained " + BASE_PLANT_EXP + " experience (level " + level + ")")
                                .withStyle(ChatFormatting.GREEN),
                        true);
            });
        }
    }

    @SubscribeEvent
    public void onBonemealUse(BonemealEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Block block = event.getBlock().getBlock();
        // Check if the block is a valid target for bonemeal
        boolean isValidTarget = (block instanceof CropBlock crop && !crop.isMaxAge(event.getBlock())) ||
                (block instanceof StemBlock &&
                        event.getBlock().getValue(StemBlock.AGE) < 7);

        if (isValidTarget) {
            // Get the player's farming capability
            player.getCapability(FarmingProvider.FARMING_CAPABILITY).ifPresent(farming -> {
                int level = farming.getLevel();

                // Add experience for using bonemeal
                farming.addExp(BASE_BONEMEAL_EXP);

                // Calculate bonemeal saving chance
                float bonemealSaveChance = level * BONEMEAL_SAVE_CHANCE_PER_LEVEL;
                if (random.nextFloat() * 100 < bonemealSaveChance) {
                    // Save the bonemeal
                    ItemStack bonemeal = new ItemStack(Items.BONE_MEAL, 1);
                    if (!player.getInventory().add(bonemeal)) {
                        player.drop(bonemeal, false);
                    }
                    player.displayClientMessage(Component.literal("Saved bonemeal").withStyle(ChatFormatting.GREEN), true);
                }

                // Display a message to the player
                player.displayClientMessage(
                        Component.literal("Gained " + BASE_BONEMEAL_EXP + " experience (level " + level + ")")
                                .withStyle(ChatFormatting.GREEN),
                        true);
            });
        }
    }
}
