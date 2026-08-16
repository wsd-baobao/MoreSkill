package com.zjw.moreskill.skill.smithing;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static com.google.common.collect.Iterables.concat;

public class SmithingHandler {

    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("36e6640b-3968-43b2-9406-b5886092f17a");

    // 为每个装备槽位定义唯一的UUID
    private static final Map<EquipmentSlot, UUID> MAX_HEALTH_MODIFIER_IDS = new HashMap<>() {{
        put(EquipmentSlot.HEAD, UUID.fromString("9d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1a"));
        put(EquipmentSlot.CHEST, UUID.fromString("8d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1b"));
        put(EquipmentSlot.LEGS, UUID.fromString("7d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1c"));
        put(EquipmentSlot.FEET, UUID.fromString("6d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1d"));
    }};

    // 按槽位独立的修饰器UUID，确保多件锻造装备的加成正确叠加
    private static final Map<EquipmentSlot, UUID> ARMOR_MODIFIER_IDS = new HashMap<>() {{
        put(EquipmentSlot.HEAD, UUID.fromString("a1b2c3d4-1101-4000-8000-000000000001"));
        put(EquipmentSlot.CHEST, UUID.fromString("a1b2c3d4-1102-4000-8000-000000000001"));
        put(EquipmentSlot.LEGS, UUID.fromString("a1b2c3d4-1103-4000-8000-000000000001"));
        put(EquipmentSlot.FEET, UUID.fromString("a1b2c3d4-1104-4000-8000-000000000001"));
    }};

    private static final Map<EquipmentSlot, UUID> TOUGHNESS_MODIFIER_IDS = new HashMap<>() {{
        put(EquipmentSlot.HEAD, UUID.fromString("a1b2c3d4-1201-4000-8000-000000000001"));
        put(EquipmentSlot.CHEST, UUID.fromString("a1b2c3d4-1202-4000-8000-000000000001"));
        put(EquipmentSlot.LEGS, UUID.fromString("a1b2c3d4-1203-4000-8000-000000000001"));
        put(EquipmentSlot.FEET, UUID.fromString("a1b2c3d4-1204-4000-8000-000000000001"));
    }};

    private static final Map<EquipmentSlot, UUID> MOVE_SPEED_MODIFIER_IDS = new HashMap<>() {{
        put(EquipmentSlot.HEAD, UUID.fromString("a1b2c3d4-1301-4000-8000-000000000001"));
        put(EquipmentSlot.CHEST, UUID.fromString("a1b2c3d4-1302-4000-8000-000000000001"));
        put(EquipmentSlot.LEGS, UUID.fromString("a1b2c3d4-1303-4000-8000-000000000001"));
        put(EquipmentSlot.FEET, UUID.fromString("a1b2c3d4-1304-4000-8000-000000000001"));
    }};

    // 旧版本共享UUID，用于清理玩家属性上可能残留的旧修饰器
    private static final UUID[] LEGACY_MODIFIER_IDS = {
            UUID.fromString("7f3b3b5a-1e8f-4a1c-9e5c-3b1c9f3b5a1e"),
            UUID.fromString("8f4c4c6b-2f9f-5b2d-ad5c-4d2c8f4c6b2f"),
            UUID.fromString("5a3b1c2d-6f8e-4d9c-b2a1-7f5c3d2b1a6e"),
            UUID.fromString("9d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1a"),
            UUID.fromString("8d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1b"),
            UUID.fromString("7d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1c"),
            UUID.fromString("6d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1d"),
    };

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        Player player = event.getEntity();
        ItemStack craftedItem = event.getCrafting();

        // 检查是否是武器、工具或盔甲
        if (craftedItem.getItem() instanceof TieredItem || craftedItem.getItem() instanceof ArmorItem) {
            // 获取玩家的锻造技能
            Smithing smithing = player.getCapability(SmithingSkillProvider.SMITHING_SKILL).orElse(null);
            if (smithing != null && smithing.getLevel() < Smithing.MAX_LEVEL) {
                // 根据物品类型给予不同经验
                int expGain;
                if (craftedItem.getItem() instanceof ArmorItem) {
                    expGain = 15; // 盔甲给予50经验
                } else if (craftedItem.getItem() instanceof SwordItem) {
                    expGain = 12; // 武器给予40经验
                } else {
                    expGain = 10; // 工具给予30经验
                }

                // 增加经验并通知玩家
                smithing.addExp(expGain);
                player.displayClientMessage(Component.translatable("message.moreskill.smithing_exp_gain", expGain).withStyle(ChatFormatting.GREEN), true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(net.minecraftforge.event.entity.EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            refreshSmithingModifiers(player);
        }
    }

    @SubscribeEvent
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            refreshSmithingModifiers(player);
        }
    }

    /**
     * 全量重算玩家身上所有锻造修饰器：
     * 先移除全部锻造修饰器（含旧版本残留），再按当前装备逐槽位重新应用，
     * 避免换装时其他槽位的锻造加成丢失。
     */
    private static void refreshSmithingModifiers(Player player) {
        removeAllSmithingModifiers(player);

        // 主手武器的攻击速度加成
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.hasTag() && mainHand.getItem() instanceof SwordItem
                && mainHand.getTag().contains(SmithingNBTManager.ATTACK_SPEED)) {
            AttributeInstance attackSpeedAttribute = player.getAttribute(Attributes.ATTACK_SPEED);
            if (attackSpeedAttribute != null) {
                float additionalAttackSpeed = mainHand.getTag().getFloat(SmithingNBTManager.ATTACK_SPEED);
                attackSpeedAttribute.addPermanentModifier(new AttributeModifier(
                        ATTACK_SPEED_MODIFIER_ID,
                        "AttackSpeedModifier",
                        additionalAttackSpeed,
                        AttributeModifier.Operation.ADDITION
                ));
            }
        }

        // 各盔甲槽位的加成
        for (EquipmentSlot slot : ARMOR_MODIFIER_IDS.keySet()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty() || !stack.hasTag()) continue;
            CompoundTag tag = stack.getTag();

            AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);
            if (armorAttribute != null && tag.contains(SmithingNBTManager.ARMOR)) {
                armorAttribute.addPermanentModifier(new AttributeModifier(
                        ARMOR_MODIFIER_IDS.get(slot),
                        "SmithingArmor." + slot.getName(),
                        tag.getFloat(SmithingNBTManager.ARMOR),
                        AttributeModifier.Operation.ADDITION
                ));
            }

            AttributeInstance toughnessAttribute = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (toughnessAttribute != null && tag.contains(SmithingNBTManager.ARMOR_TOUGHNESS)) {
                toughnessAttribute.addPermanentModifier(new AttributeModifier(
                        TOUGHNESS_MODIFIER_IDS.get(slot),
                        "SmithingToughness." + slot.getName(),
                        tag.getFloat(SmithingNBTManager.ARMOR_TOUGHNESS),
                        AttributeModifier.Operation.ADDITION
                ));
            }

            AttributeInstance maxHealthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttribute != null && tag.contains(SmithingNBTManager.MAX_HEALTH)) {
                maxHealthAttribute.addPermanentModifier(new AttributeModifier(
                        MAX_HEALTH_MODIFIER_IDS.get(slot),
                        "SmithingMaxHealth." + slot.getName(),
                        tag.getFloat(SmithingNBTManager.MAX_HEALTH),
                        AttributeModifier.Operation.ADDITION
                ));
            }

            AttributeInstance moveSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (moveSpeedAttribute != null && tag.contains(SmithingNBTManager.MOVE_SPEED)) {
                moveSpeedAttribute.addPermanentModifier(new AttributeModifier(
                        MOVE_SPEED_MODIFIER_IDS.get(slot),
                        "SmithingMoveSpeed." + slot.getName(),
                        tag.getFloat(SmithingNBTManager.MOVE_SPEED) / 100.0f,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
            }
        }
    }

    /**
     * 移除玩家身上所有锻造修饰器（当前按槽位UUID + 旧版本共享UUID），
     * 供全量重算使用。
     */
    private static void removeAllSmithingModifiers(Player player) {
        AttributeInstance attackSpeedAttribute = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute != null) {
            attackSpeedAttribute.removeModifier(ATTACK_SPEED_MODIFIER_ID);
        }

        for (EquipmentSlot slot : ARMOR_MODIFIER_IDS.keySet()) {
            AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);
            if (armorAttribute != null) {
                armorAttribute.removeModifier(ARMOR_MODIFIER_IDS.get(slot));
            }
            AttributeInstance toughnessAttribute = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (toughnessAttribute != null) {
                toughnessAttribute.removeModifier(TOUGHNESS_MODIFIER_IDS.get(slot));
            }
            AttributeInstance maxHealthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttribute != null) {
                maxHealthAttribute.removeModifier(MAX_HEALTH_MODIFIER_IDS.get(slot));
            }
            AttributeInstance moveSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (moveSpeedAttribute != null) {
                moveSpeedAttribute.removeModifier(MOVE_SPEED_MODIFIER_IDS.get(slot));
            }
        }

        // 清理旧版本可能残留的共享UUID修饰器
        for (UUID legacyId : LEGACY_MODIFIER_IDS) {
            AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);
            if (armorAttribute != null) {
                armorAttribute.removeModifier(legacyId);
            }
            AttributeInstance toughnessAttribute = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (toughnessAttribute != null) {
                toughnessAttribute.removeModifier(legacyId);
            }
            AttributeInstance maxHealthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttribute != null) {
                maxHealthAttribute.removeModifier(legacyId);
            }
            AttributeInstance moveSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (moveSpeedAttribute != null) {
                moveSpeedAttribute.removeModifier(legacyId);
            }
        }
    }

    @SubscribeEvent
    public void onAttached(LivingHurtEvent event) {
        // 检查攻击者是否为玩家
        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack heldItem = player.getMainHandItem();
            // 仅对近战武器（剑）和远程武器（弓）生效
            if (heldItem.getItem() instanceof SwordItem || heldItem.getItem() instanceof BowItem) {
                // 检查是否有暴击率标签
                if (heldItem.hasTag() && heldItem.getTag().contains(SmithingNBTManager.CRITICAL_STRIKE_CHANCE)) {
                    float criticalStrikeChance = heldItem.getTag().getFloat(SmithingNBTManager.CRITICAL_STRIKE_CHANCE);

                    // 随机判定是否暴击
                    if (player.getRandom().nextFloat() * 100 < criticalStrikeChance) {
                        // 暴击效果：伤害翻倍
                        float originalDamage = event.getAmount();
                        float criticalDamage = originalDamage * 2f;

                        // 设置暴击伤害
                        event.setAmount(criticalDamage);

                        event.getEntity().level().playSound(null, event.getEntity().blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

                    }
                }

                // 原有的攻击伤害逻辑
                event.setAmount(event.getAmount() + heldItem.getOrCreateTag().getInt(SmithingNBTManager.ATTACK_DAMAGE));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        playerLastAbsorptionTime.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 只处理玩家 Tick 阶段为 END 的情况
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;

        // 每 20 tick(1秒)才扫描一次物品栏，避免每 tick 全物品栏 tag 检查的开销
        if (player.tickCount % 20 != 0) {
            return;
        }

        // 遍历玩家的所有装备和物品栏
        Iterable<ItemStack> allItems = concat(
                player.getInventory().items,
                player.getInventory().armor,
                player.getInventory().offhand
        );

        // 遍历所有物品并尝试修复
        for (ItemStack inventoryItem : allItems) {
            // 检查物品是否有额外耐久
            if (inventoryItem.hasTag() &&
                    inventoryItem.getTag().contains(SmithingNBTManager.DURABILITY)) {
                // 尝试修复物品
                repairItem(inventoryItem);
            }
        }

        UUID playerId = player.getUUID();
        long currentTime = player.level().getGameTime();

        // 检查是否需要转换吸收值为吸收心
        Long lastAbsorptionTime = playerLastAbsorptionTime.get(playerId);
        if (lastAbsorptionTime != null && currentTime - lastAbsorptionTime >= ABSORPTION_CONVERSION_DELAY) {
            float totalAbsorption = 0f;
            boolean hasAbsorption = false;

            // 收集并重置所有装备的吸收值
            for (ItemStack armorStack : player.getArmorSlots()) {
                if (armorStack.hasTag() && armorStack.getTag().contains(SmithingNBTManager.ABSORPTION)) {
                    CompoundTag tag = armorStack.getTag();
                    float absorption = tag.getFloat(SmithingNBTManager.ABSORPTION);
                    if (absorption > 0) {
                        hasAbsorption = true;
                        totalAbsorption += absorption;
                        // 重置吸收值
                        tag.putFloat(SmithingNBTManager.ABSORPTION, 0f);
                    }
                }
            }

            if (hasAbsorption) {
                // 设置1分钟的吸收心效果
                int absorptionDuration = 20 * 60; // 1分钟，以tick为单位
                int amplifier = Math.min((int) (totalAbsorption / 2), 4); // 等级限制在0-4之间

                // 如果已经有吸收效果，选择较高的等级
                MobEffectInstance currentEffect = player.getEffect(MobEffects.ABSORPTION);
                if (currentEffect != null) {
                    amplifier = Math.max(currentEffect.getAmplifier(), amplifier);
                }

                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, absorptionDuration, amplifier, false, false));

                // 显示转换提示
                player.displayClientMessage(Component.translatable("message.moreskill.absorption_conversion", 1).withStyle(ChatFormatting.GOLD), true);

                // 清除记录
                playerLastAbsorptionTime.remove(playerId);
            }
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        // 仅对挖掘工具生效
        if (heldItem.getItem() instanceof DiggerItem &&
                heldItem.hasTag() &&
                heldItem.getTag().contains(SmithingNBTManager.BREAK_SPEED)) {

            float additionalBreakSpeed = heldItem.getTag().getFloat(SmithingNBTManager.BREAK_SPEED);

            // 根据工具类型和方块类型调整加成
            Block targetBlock = event.getState().getBlock();
            if (heldItem.getItem() instanceof PickaxeItem && targetBlock.defaultBlockState().requiresCorrectToolForDrops()) {
                // 对需要正确工具的方块额外加成
                additionalBreakSpeed *= 1.5f;
            }
            event.setNewSpeed(event.getOriginalSpeed() * (1 + additionalBreakSpeed / 100f));
        }
    }

    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {
        // 检查是否是玩家受到攻击
        if (event.getEntity() instanceof Player player) {
            // 遍历装备并更新吸收值
            for (ItemStack armorStack : player.getArmorSlots()) {
                if (armorStack.hasTag() && armorStack.getTag().contains(SmithingNBTManager.ABSORPTION)) {
                    CompoundTag tag = armorStack.getOrCreateTag();
                    float currentAbsorption = tag.getFloat(SmithingNBTManager.ABSORPTION);
                    // 增加吸收值，根据受到的伤害计算
                    float newAbsorption = currentAbsorption + (event.getAmount() * 0.5f); // 50%的伤害转化为吸收值
                    tag.putFloat(SmithingNBTManager.ABSORPTION, newAbsorption);

                    // 记录最后受到伤害的时间
                    playerLastAbsorptionTime.put(player.getUUID(), player.level().getGameTime());

                    // 显示吸收值获取提示
                    player.displayClientMessage(Component.translatable("message.moreskill.absorption_gained", String.format("%.1f", newAbsorption)).withStyle(ChatFormatting.GOLD), true);
                }
            }

            // 原有的荆棘效果逻辑
            for (ItemStack armorStack : player.getArmorSlots()) {
                if (armorStack.hasTag() && armorStack.getTag().contains(SmithingNBTManager.THORNS)) {
                    float thornsValue = armorStack.getTag().getFloat(SmithingNBTManager.THORNS);

                    if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                        float thornsDamage = event.getAmount() * (thornsValue / 100.0f);
                        attacker.hurt(player.damageSources().thorns(player), thornsDamage);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onKnockback(LivingHurtEvent event) {
        // 检查是否是玩家受到攻击
        if (event.getEntity() instanceof Player player) {
            // 检查玩家的所有装备
            Iterable<ItemStack> armorSlots = player.getArmorSlots();
            float totalKnockbackResistance = 0f;
            for (ItemStack armorPiece : armorSlots) {
                if (armorPiece.hasTag() && armorPiece.getTag().contains(SmithingNBTManager.KNOCKBACK_RESISTANCE)) {
                    // 累加抗击退值
                    totalKnockbackResistance += armorPiece.getTag().getFloat(SmithingNBTManager.KNOCKBACK_RESISTANCE);
                }
            }
            // 如果有抗击退值，减少击退效果
            if (totalKnockbackResistance > 0) {
                // 计算击退抵抗
                // 每50点抗击退值减少10%击退
                float knockbackReductionFactor = 1 - (totalKnockbackResistance / 500f);

                // 确保不会完全免疫击退
                knockbackReductionFactor = Math.max(0.1f, knockbackReductionFactor);

                // 应用击退抵抗
                if (event.getSource().getDirectEntity() != null) {
                    // 获取击退向量
                    Vec3 knockbackVector = event.getSource().getDirectEntity().getDeltaMovement();

                    // 缩放击退向量
                    player.setDeltaMovement(
                            knockbackVector.x * knockbackReductionFactor,
                            knockbackVector.y * knockbackReductionFactor,
                            knockbackVector.z * knockbackReductionFactor
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        CompoundTag tag = itemStack.getTag();
        List<Component> tooltips = event.getToolTip();

        if (tag != null) {
            Map<String, Supplier<Component>> tooltipMap = new HashMap<>();
            tooltipMap.put(SmithingNBTManager.AUTHOR, () -> Component.translatable("tooltip.smithing.author", tag.getString(SmithingNBTManager.AUTHOR)).withStyle(ChatFormatting.GOLD));
            tooltipMap.put(SmithingNBTManager.ATTACK_DAMAGE, () -> Component.translatable("tooltip.smithing.attack_damage", tag.getInt(SmithingNBTManager.ATTACK_DAMAGE)).withStyle(ChatFormatting.BLUE));
            tooltipMap.put(SmithingNBTManager.ATTACK_SPEED, () -> Component.translatable("tooltip.smithing.attack_speed", tag.getInt(SmithingNBTManager.ATTACK_SPEED)).withStyle(ChatFormatting.BLUE));
            tooltipMap.put(SmithingNBTManager.DURABILITY, () -> Component.translatable("tooltip.smithing.durability", tag.getInt(SmithingNBTManager.DURABILITY)).withStyle(ChatFormatting.BLUE));
            tooltipMap.put(SmithingNBTManager.ARMOR, () -> Component.translatable("tooltip.smithing.armor", tag.getInt(SmithingNBTManager.ARMOR)).withStyle(ChatFormatting.BLUE));
            tooltipMap.put(SmithingNBTManager.ARMOR_TOUGHNESS, () -> Component.translatable("tooltip.smithing.armor_toughness", tag.getInt(SmithingNBTManager.ARMOR_TOUGHNESS)).withStyle(ChatFormatting.BLUE));
            tooltipMap.put(SmithingNBTManager.BREAK_SPEED, () -> Component.translatable("tooltip.smithing.break_speed", tag.getInt(SmithingNBTManager.BREAK_SPEED)).withStyle(ChatFormatting.DARK_AQUA));
            tooltipMap.put(SmithingNBTManager.KNOCKBACK_RESISTANCE, () -> Component.translatable("tooltip.smithing.knockback_resistance", tag.getInt(SmithingNBTManager.KNOCKBACK_RESISTANCE)).withStyle(ChatFormatting.BLUE));
            tooltipMap.put(SmithingNBTManager.CRITICAL_STRIKE_CHANCE, () -> Component.translatable("tooltip.smithing.critical_strike_chance", tag.getFloat(SmithingNBTManager.CRITICAL_STRIKE_CHANCE)).withStyle(ChatFormatting.RED));
            tooltipMap.put(SmithingNBTManager.MAX_HEALTH, () -> Component.translatable("tooltip.smithing.max_health", tag.getInt(SmithingNBTManager.MAX_HEALTH)).withStyle(ChatFormatting.GREEN));
            tooltipMap.put(SmithingNBTManager.MOVE_SPEED, () -> Component.translatable("tooltip.smithing.move_speed", tag.getFloat(SmithingNBTManager.MOVE_SPEED)).withStyle(ChatFormatting.AQUA));
            tooltipMap.put(SmithingNBTManager.ABSORPTION, () -> Component.translatable("tooltip.smithing.absorption", tag.getFloat(SmithingNBTManager.ABSORPTION)).withStyle(ChatFormatting.GOLD));
            tooltipMap.put(SmithingNBTManager.THORNS, () -> Component.translatable("tooltip.smithing.thorns", tag.getFloat(SmithingNBTManager.THORNS)).withStyle(ChatFormatting.RED));

            for (String key : tooltipMap.keySet()) {
                if (tag.contains(key)) {
                    tooltips.add(tooltipMap.get(key).get());
                }
            }
        }
    }


    private static final Map<UUID, Long> playerLastAbsorptionTime = new HashMap<>();
    private static final long ABSORPTION_CONVERSION_DELAY = 20 * 5; // 5秒后转换


    private void repairItem(ItemStack itemStack) {
        int currentDurability = itemStack.getDamageValue(); // 当前损耗的耐久值
        // 获取额外耐久值
        int extraDurability = itemStack.getOrCreateTag().getInt(SmithingNBTManager.DURABILITY);

        // 如果没有额外耐久，直接返回
        if (extraDurability <= 0) {
            return;
        }
        // 计算可以修复的耐久量
        int repairAmount = Math.min(extraDurability, currentDurability);

        // 计算新的损耗值
        int newDurability = Math.max(0, currentDurability - repairAmount);

        // 更新物品的耐久值
        itemStack.setDamageValue(newDurability);

        // 扣除使用的额外耐久
        int remainingDurability = extraDurability - repairAmount;
        itemStack.getOrCreateTag().putInt(SmithingNBTManager.DURABILITY, Math.max(remainingDurability, 0));
    }
}
