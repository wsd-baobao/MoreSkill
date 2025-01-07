package com.zjw.moreskill.skill.combat;

import com.zjw.moreskill.MoreSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CombatHandler {
    // Totem of Undying cooldown tracking
    private static final Map<UUID, Long> totemCooldownMap = new HashMap<>();
    // Dodge related data
    private static final Map<UUID, Long> dodgeCooldownMap = new HashMap<>();
    // Parry related data
    private static final Map<UUID, Long> parryCooldownMap = new HashMap<>();
    // Dash related data
    private static final Map<UUID, Long> dashCooldownMap = new HashMap<>();

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
    // Dash related constants
    private static final double BASE_DASH_DISTANCE = 4.0; // Base dash distance
    private static final long DASH_COOLDOWN = 5000; // Dash cooldown time (milliseconds)

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        // Check if the entity being hurt is a player
        if (event.getEntity() instanceof Player player) {
            player.getCapability(CombatProvider.COMBAT_CAPABILITY).ifPresent(combat -> {
                long currentTime = System.currentTimeMillis();
                long cooldown = calculateTotemCooldown(combat.getLevel());
                Long lastTotemUseTime = totemCooldownMap.get(player.getUUID());

                // Check if the player can use the Totem of Undying skill
                if (lastTotemUseTime == null || currentTime - lastTotemUseTime >= cooldown) {
                    // Check if the player's health is low enough to trigger the skill
                    if (player.getHealth() <= event.getAmount()) {
                        // Activate the Totem of Undying skill
                        player.setHealth(player.getMaxHealth() * (0.4f + 0.01f * combat.getLevel()));
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                                900 + (combat.getLevel() * 100),
                                1));
                        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,
                                100 + (combat.getLevel() * 20),
                                1));
                        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,
                                800 + (combat.getLevel() * 100),
                                0));

                        // Update the cooldown for the Totem of Undying skill
                        totemCooldownMap.put(player.getUUID(), currentTime);

                        // Add combat experience to the player
                        combat.addCombatExp(player, 10);

                        // Cancel the hurt event
                        event.setCanceled(true);

                        // Log the activation of the Totem of Undying skill
                        MoreSkill.LOGGER.info("Player {} activated Totem of Undying skill at level {}",
                                player.getName().getString(), combat.getLevel());
                    }
                }

                // Check if the player can parry the attack
                Long lastParryTime = parryCooldownMap.get(player.getUUID());

                // Check if the player is holding a sword
                boolean holdingSword = player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwordItem;

                // Check if the player can parry the attack
                if (holdingSword && (lastParryTime == null || currentTime - lastParryTime >= PARRY_COOLDOWN)) {
                    double parryChance = calculateParryChance(combat.getLevel());

                    // Check if the player successfully parries the attack
                    if (Math.random() < parryChance) {
                        // Cancel the hurt event
                        event.setCanceled(true);

                        // Update the cooldown for the parry
                        parryCooldownMap.put(player.getUUID(), currentTime);

                        // Add combat experience to the player
                        combat.addCombatExp(player, 7);

                        // Check if the attacker is a living entity
                        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                            // Calculate the counter damage
                            float counterDamage = calculateCounterDamage(combat.getLevel());

                            // Deal the counter damage to the attacker
                            DamageSource counterAttackSource = player.level().damageSources().playerAttack(player);
                            attacker.hurt(counterAttackSource, counterDamage);

                            // Log the successful parry and counter-attack
                            MoreSkill.LOGGER.info(
                                    "Player {} successfully parried and counter-attacked at combat level {}",
                                    player.getName().getString(), combat.getLevel());
                        }

                        // TODO: Implement additional effects for a successful parry
                    }
                }

                // Check if the player can dodge the attack
                Long lastDodgeTime = dodgeCooldownMap.get(player.getUUID());

                // Check if the player can dodge the attack
                if (lastDodgeTime == null || currentTime - lastDodgeTime >= DODGE_COOLDOWN) {
                    double dodgeChance = calculateDodgeChance(combat.getLevel());

                    // Check if the player successfully dodges the attack
                    if (Math.random() < dodgeChance) {
                        // Cancel the hurt event
                        event.setCanceled(true);

                        // Update the cooldown for the dodge
                        dodgeCooldownMap.put(player.getUUID(), currentTime);

                        // Add combat experience to the player
                        combat.addCombatExp(player, 5);

                        // Log the successful dodge
                        MoreSkill.LOGGER.info("Player {} successfully dodged an attack at combat level {}",
                                player.getName().getString(), combat.getLevel());

                        // TODO: Implement additional effects for a successful dodge
                    }
                }
            });
        }

        // Check if the attacker is a player
        if (event.getSource().getEntity() instanceof Player player) {
            // Get the combat capability of the player
            Combat combat = player.getCapability(CombatProvider.COMBAT_CAPABILITY).orElse(null);
            int combatLevel = combat.getLevel();

            // Apply life steal
            applyLifeSteal(player, combatLevel, event.getAmount());

            // Update the damage amount
            event.setAmount(event.getAmount());
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
