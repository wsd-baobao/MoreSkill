package com.zjw.moreskill.attribute;

import com.zjw.moreskill.MoreSkill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttributeEffectHandler {

    private static final UUID STR_ATK_UUID = UUID.fromString("a1b2c3d4-0001-4000-8000-000000000001");
    private static final UUID STR_ARMOR_UUID = UUID.fromString("a1b2c3d4-0001-4000-8000-000000000002");
    private static final UUID AGI_SPEED_UUID = UUID.fromString("a1b2c3d4-0002-4000-8000-000000000001");
    private static final UUID AGI_ATKSPEED_UUID = UUID.fromString("a1b2c3d4-0002-4000-8000-000000000002");
    private static final UUID VIT_TOUGHNESS_UUID = UUID.fromString("a1b2c3d4-0003-4000-8000-000000000001");
    private static final UUID VIT_KB_UUID = UUID.fromString("a1b2c3d4-0003-4000-8000-000000000002");

    // ===== Lifecycle =====

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            applyAllModifiers(player);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.getEntity().level().isClientSide) {
            applyAllModifiers(event.getEntity());
        }
    }

    // ===== Modifier Application =====

    public static void applyAllModifiers(Player player) {
        player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> {
            int str = data.getPoints(ModAttribute.STRENGTH);
            int agi = data.getPoints(ModAttribute.AGILITY);
            int vit = data.getPoints(ModAttribute.VITALITY);

            applyMod(player, Attributes.ATTACK_DAMAGE, STR_ATK_UUID,
                    AttributeEffects.getAttackDamageBonus(str), "moreskill.str.atk", AttributeModifier.Operation.ADDITION);
            applyMod(player, Attributes.ARMOR, STR_ARMOR_UUID,
                    AttributeEffects.getArmorBonus(str), "moreskill.str.armor", AttributeModifier.Operation.ADDITION);
            applyMod(player, Attributes.MOVEMENT_SPEED, AGI_SPEED_UUID,
                    AttributeEffects.getSpeedBonus(agi), "moreskill.agi.speed", AttributeModifier.Operation.MULTIPLY_BASE);
            applyMod(player, Attributes.ATTACK_SPEED, AGI_ATKSPEED_UUID,
                    AttributeEffects.getAttackSpeedBonus(agi), "moreskill.agi.atkspeed", AttributeModifier.Operation.MULTIPLY_BASE);
            applyMod(player, Attributes.ARMOR_TOUGHNESS, VIT_TOUGHNESS_UUID,
                    AttributeEffects.getArmorToughnessBonus(vit), "moreskill.vit.tough", AttributeModifier.Operation.ADDITION);
            applyMod(player, Attributes.KNOCKBACK_RESISTANCE, VIT_KB_UUID,
                    AttributeEffects.getKnockbackResistanceBonus(vit), "moreskill.vit.kb", AttributeModifier.Operation.ADDITION);
        });
    }

    private static void applyMod(Player player, net.minecraft.world.entity.ai.attributes.Attribute attr,
                                  UUID uuid, double value, String name, AttributeModifier.Operation op) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst == null) return;
        if (inst.getModifier(uuid) != null) inst.removeModifier(uuid);
        if (value != 0) inst.addPermanentModifier(new AttributeModifier(uuid, name, value, op));
    }

    // ===== Custom Event Effects =====

    @SubscribeEvent
    public void onPlayerAttack(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> {
            int agi = data.getPoints(ModAttribute.AGILITY);
            int luck = data.getPoints(ModAttribute.LUCK);
            int str = data.getPoints(ModAttribute.STRENGTH);

            double critChance = AttributeEffects.getCritRateBonus(agi) + AttributeEffects.getLuckCritBonus(luck);
            if (critChance > 0 && Math.random() < critChance) {
                double critMulti = 1.5 + AttributeEffects.getCriticalDamageBonus(str);
                event.setAmount(event.getAmount() * (float) critMulti);
            }
        });
    }

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> {
            int agi = data.getPoints(ModAttribute.AGILITY);
            int luck = data.getPoints(ModAttribute.LUCK);

            double dodgeChance = AttributeEffects.getDodgeChance(agi) + AttributeEffects.getLuckDodgeBonus(luck);
            if (dodgeChance > 0 && Math.random() < dodgeChance) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> {
            int luck = data.getPoints(ModAttribute.LUCK);
            double dropBonus = AttributeEffects.getMobDropBonus(luck);
            if (dropBonus > 0 && Math.random() < dropBonus) {
                LivingEntity entity = event.getEntity();
                LootTable lootTable = entity.level().getServer().getLootData()
                        .getElement(net.minecraft.world.level.storage.loot.LootDataType.TABLE, entity.getLootTable());
                if (lootTable != null) {
                    LootParams lootParams = new LootParams.Builder((net.minecraft.server.level.ServerLevel) entity.level())
                            .withParameter(LootContextParams.ORIGIN, entity.position())
                            .withParameter(LootContextParams.THIS_ENTITY, entity)
                            .create(LootContextParamSets.ENTITY);
                    lootTable.getRandomItemsRaw(lootParams, itemStack -> entity.spawnAtLocation(itemStack));
                }
            }
        });
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide) return;

        player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> {
            int luck = data.getPoints(ModAttribute.LUCK);
            double miningBonus = AttributeEffects.getMiningDropBonus(luck);
            if (miningBonus > 0 && Math.random() < miningBonus) {
                event.getPlayer().level().getBlockState(event.getPos())
                        .getBlock().getDrops(event.getPlayer().level().getBlockState(event.getPos()),
                                (net.minecraft.server.level.ServerLevel) player.level(),
                                event.getPos(), null, player, player.getMainHandItem())
                        .forEach(drop -> {
                            net.minecraft.world.entity.item.ItemEntity itemEntity =
                                    new net.minecraft.world.entity.item.ItemEntity(player.level(),
                                            event.getPos().getX() + 0.5,
                                            event.getPos().getY() + 0.5,
                                            event.getPos().getZ() + 0.5,
                                            drop);
                            player.level().addFreshEntity(itemEntity);
                        });
            }
        });
    }

    @SubscribeEvent
    public void onFishing(ItemFishedEvent event) {
        Player player = (Player) event.getEntity();
        if (player.level().isClientSide) return;

        player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> {
            int luck = data.getPoints(ModAttribute.LUCK);
            double fishingBonus = AttributeEffects.getFishingBonus(luck);
            if (fishingBonus > 0 && Math.random() < fishingBonus) {
                event.getDrops().forEach(stack -> {
                    stack.setCount(stack.getCount() + 1);
                });
            }
        });
    }

    @SubscribeEvent
    public void onXpPickup(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> {
            int luck = data.getPoints(ModAttribute.LUCK);
            double xpBonus = AttributeEffects.getXpGainBonus(luck);
            if (xpBonus > 0) {
                net.minecraft.world.entity.ExperienceOrb orb = event.getOrb();
                int bonus = (int) (orb.value * xpBonus);
                if (bonus > 0) {
                    orb.value += bonus;
                }
            }
        });
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> {
            int vit = data.getPoints(ModAttribute.VITALITY);
            double regenBonus = AttributeEffects.getHealthRegenBonus(vit);
            if (regenBonus > 0 && player.tickCount % 20 == 0) {
                float heal = (float) (regenBonus * player.getMaxHealth() * 0.05);
                if (heal > 0 && player.getHealth() < player.getMaxHealth()) {
                    player.heal(heal);
                }
            }
        });
    }
}
