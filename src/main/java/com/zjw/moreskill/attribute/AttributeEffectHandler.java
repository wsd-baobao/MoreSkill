package com.zjw.moreskill.attribute;

import com.zjw.moreskill.MoreSkill;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttributeEffectHandler {

    private static final UUID STRENGTH_UUID = UUID.fromString("a1b2c3d4-1111-4000-8000-000000000001");
    private static final UUID AGILITY_UUID = UUID.fromString("a1b2c3d4-2222-4000-8000-000000000002");
    private static final UUID VITALITY_UUID = UUID.fromString("a1b2c3d4-3333-4000-8000-000000000003");
    private static final UUID LUCK_UUID = UUID.fromString("a1b2c3d4-4444-4000-8000-000000000004");

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!player.level().isClientSide) {
                applyAllModifiers(player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.getEntity().level().isClientSide) {
            applyAllModifiers(event.getEntity());
        }
    }

    public static void applyAllModifiers(Player player) {
        player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> {
            applyModifier(player, Attributes.ATTACK_DAMAGE, STRENGTH_UUID,
                    AttributeEffects.getAttackDamageBonus(data.getPoints(ModAttribute.STRENGTH)),
                    "moreskill.strength", AttributeModifier.Operation.ADDITION);

            applyModifier(player, Attributes.MOVEMENT_SPEED, AGILITY_UUID,
                    AttributeEffects.getSpeedBonus(data.getPoints(ModAttribute.AGILITY)),
                    "moreskill.agility", AttributeModifier.Operation.MULTIPLY_BASE);

            applyModifier(player, Attributes.MAX_HEALTH, VITALITY_UUID,
                    AttributeEffects.getMaxHealthBonus(data.getPoints(ModAttribute.VITALITY)),
                    "moreskill.vitality", AttributeModifier.Operation.ADDITION);

            applyModifier(player, Attributes.LUCK, LUCK_UUID,
                    AttributeEffects.getLuckBonus(data.getPoints(ModAttribute.LUCK)),
                    "moreskill.luck", AttributeModifier.Operation.ADDITION);

            float newMaxHealth = (float) player.getAttributeValue(Attributes.MAX_HEALTH);
            if (player.getHealth() > newMaxHealth) {
                player.setHealth(newMaxHealth);
            }
        });
    }

    public static void removeAttributeModifiers(Player player) {
        removeModifier(player, Attributes.ATTACK_DAMAGE, STRENGTH_UUID);
        removeModifier(player, Attributes.MOVEMENT_SPEED, AGILITY_UUID);
        removeModifier(player, Attributes.MAX_HEALTH, VITALITY_UUID);
        removeModifier(player, Attributes.LUCK, LUCK_UUID);
    }

    private static void applyModifier(Player player, net.minecraft.world.entity.ai.attributes.Attribute attribute,
                                       UUID uuid, double value, String name, AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        AttributeModifier existing = instance.getModifier(uuid);
        if (existing != null) {
            instance.removeModifier(uuid);
        }

        if (value != 0) {
            instance.addPermanentModifier(new AttributeModifier(uuid, name, value, operation));
        }
    }

    private static void removeModifier(Player player, net.minecraft.world.entity.ai.attributes.Attribute attribute, UUID uuid) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(uuid);
        }
    }
}
