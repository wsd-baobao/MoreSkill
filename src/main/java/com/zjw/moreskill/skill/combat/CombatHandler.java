package com.zjw.moreskill.skill.combat;

import com.zjw.moreskill.MoreSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatHandler {
    // Totem of Undying cooldown tracking
    private static final Map<UUID, Long> totemCooldownMap = new ConcurrentHashMap<>();
    // Dodge related data
    private static final Map<UUID, Long> dodgeCooldownMap = new ConcurrentHashMap<>();
    // Parry related data
    private static final Map<UUID, Long> parryCooldownMap = new ConcurrentHashMap<>();


    // Base cooldown in milliseconds
    private static final long BASE_TOTEM_COOLDOWN = 60000; // 1 minute
    // Cooldown reduction per level (milliseconds)
    private static final long COOLDOWN_REDUCTION_PER_LEVEL = 2000; // 2 seconds
    // Dodge related constants
    private static final double BASE_DODGE_CHANCE = 0.05; // Base dodge chance
    private static final double MAX_DODGE_CHANCE = 0.5; // Maximum dodge chance
    private static final long DODGE_COOLDOWN = 1000; // Dodge cooldown time (milliseconds)
    // Parry related constants
    private static final double BASE_PARRY_CHANCE = 0.1; // Base parry chance
    private static final double MAX_PARRY_CHANCE = 0.6; // Maximum parry chance
    private static final long PARRY_COOLDOWN = 1500; // Parry cooldown time (milliseconds)

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerId = event.getEntity().getUUID();
        totemCooldownMap.remove(playerId);
        dodgeCooldownMap.remove(playerId);
        parryCooldownMap.remove(playerId);
    }

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        // 处理玩家被伤害的情况（防御机制）
        if (event.getEntity() instanceof Player player) {
            player.getCapability(CombatProvider.COMBAT_CAPABILITY).ifPresent(combat -> {
                long currentTime = System.currentTimeMillis();
                combat.addCombatExp(1);

                // 1. 优先检查不死图腾（濒死触发）
                if (combat.getLevel() > 50 && player.getHealth() <= event.getAmount()) {
                    long cooldown = calculateTotemCooldown(combat.getLevel());
                    Long lastTotemUseTime = totemCooldownMap.get(player.getUUID());
                    if (lastTotemUseTime == null || currentTime - lastTotemUseTime >= cooldown) {
                        player.setHealth(player.getMaxHealth() * (0.4f + 0.01f * combat.getLevel()));
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900 + (combat.getLevel() * 100), 1));
                        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100 + (combat.getLevel() * 20), 1));
                        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800 + (combat.getLevel() * 100), 0));
                        totemCooldownMap.put(player.getUUID(), currentTime);
                        combat.addCombatExp(10);
                        event.setCanceled(true);
                        MoreSkill.LOGGER.info("Player {} activated Totem of Undying skill at level {}",
                                player.getName().getString(), combat.getLevel());
                        return;
                    }
                }

                // 2. 检查格挡（需要持剑）
                boolean holdingSword = player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwordItem;
                if (holdingSword) {
                    Long lastParryTime = parryCooldownMap.get(player.getUUID());
                    if (lastParryTime == null || currentTime - lastParryTime >= PARRY_COOLDOWN) {
                        double parryChance = calculateParryChance(combat.getLevel());
                        if (Math.random() < parryChance) {
                            event.setCanceled(true);
                            parryCooldownMap.put(player.getUUID(), currentTime);
                            combat.addCombatExp(7);
                            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                                float counterDamage = calculateCounterDamage(combat.getLevel());
                                attacker.hurt(player.level().damageSources().playerAttack(player), counterDamage);
                            }
                            MoreSkill.LOGGER.info("Player {} successfully parried and counter-attacked at combat level {}",
                                    player.getName().getString(), combat.getLevel());
                            return;
                        }
                    }
                }

                // 3. 检查闪避
                Long lastDodgeTime = dodgeCooldownMap.get(player.getUUID());
                if (lastDodgeTime == null || currentTime - lastDodgeTime >= DODGE_COOLDOWN) {
                    double dodgeChance = calculateDodgeChance(combat.getLevel());
                    if (Math.random() < dodgeChance) {
                        event.setCanceled(true);
                        dodgeCooldownMap.put(player.getUUID(), currentTime);
                        combat.addCombatExp(5);
                        MoreSkill.LOGGER.info("Player {} successfully dodged an attack at combat level {}",
                                player.getName().getString(), combat.getLevel());
                        return;
                    }
                }
            });
        }

        // 事件已被取消则不再处理后续逻辑
        if (event.isCanceled()) return;

        // 处理玩家攻击其他实体的情况（吸血 + 攻击经验）
        if (event.getSource().getEntity() instanceof Player player) {
            player.getCapability(CombatProvider.COMBAT_CAPABILITY).ifPresent(combat -> {
                combat.addCombatExp(1);
                applyLifeSteal(player, combat.getLevel(), event.getAmount());
            });
        }
    }

    /**
     * Calculate the cooldown for the Totem of Undying skill based on the combat level
     *
     * @param combatLevel The combat level of the player
     * @return The cooldown for the Totem of Undying skill
     */
    private long calculateTotemCooldown(int combatLevel) {
        // Calculate the cooldown based on the combat level
        long reducedCooldown = BASE_TOTEM_COOLDOWN - (combatLevel * COOLDOWN_REDUCTION_PER_LEVEL);
        return Math.max(reducedCooldown, 10000);
    }

    /**
     * Calculate the dodge chance based on the combat level
     *
     * @param combatLevel The combat level of the player
     * @return The dodge chance
     */
    private double calculateDodgeChance(int combatLevel) {
        // Calculate the dodge chance based on the combat level
        double dodgeChance = BASE_DODGE_CHANCE + (combatLevel * 0.003);
        return Math.min(dodgeChance, MAX_DODGE_CHANCE);
    }

    /**
     * Calculate the parry chance based on the combat level
     *
     * @param combatLevel The combat level of the player
     * @return The parry chance
     */
    private double calculateParryChance(int combatLevel) {
        // Calculate the parry chance based on the combat level
        double parryChance = BASE_PARRY_CHANCE + (combatLevel * 0.004);
        return Math.min(parryChance, MAX_PARRY_CHANCE);
    }

    /**
     * Calculate the counter damage based on the combat level
     *
     * @param combatLevel The combat level of the player
     * @return The counter damage
     */
    private float calculateCounterDamage(int combatLevel) {
        // Calculate the counter damage based on the combat level
        return 2.0f + (combatLevel * 0.1f);
    }

    /**
     * Calculate the life steal amount based on the combat level and damage
     *
     * @param combatLevel The combat level of the player
     * @param damage The damage amount
     * @return The life steal amount
     */
    private float calculateLifeSteal(int combatLevel, float damage) {
        // Calculate the life steal amount based on the combat level and damage
        double lifeStealRate = Math.min(combatLevel * 0.002, 0.3);
        return (float) (damage * lifeStealRate);
    }

    /**
     * Apply life steal to the player
     *
     * @param player The player
     * @param combatLevel The combat level of the player
     * @param damage The damage amount
     */
    private void applyLifeSteal(Player player, int combatLevel, float damage) {
        float lifeSteal = calculateLifeSteal(combatLevel, damage);
        if (lifeSteal > 0) {
            // Apply life steal to the player
            float currentHealth = player.getHealth();
            float maxHealth = player.getMaxHealth();
            player.setHealth(Math.min(currentHealth + lifeSteal, maxHealth));
        }
    }
}
