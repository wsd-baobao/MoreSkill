package com.zjw.moreskill.skill.smithing;

import com.zjw.moreskill.MoreSkill;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.collect.Iterables.concat;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SmithingHandler {

    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("36e6640b-3968-43b2-9406-b5886092f17a");
    
    // 为每个装备槽位定义唯一的UUID
    private static final Map<EquipmentSlot, UUID> MAX_HEALTH_MODIFIER_IDS = new HashMap<>() {{
        put(EquipmentSlot.HEAD, UUID.fromString("9d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1a"));
        put(EquipmentSlot.CHEST, UUID.fromString("8d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1b"));
        put(EquipmentSlot.LEGS, UUID.fromString("7d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1c"));
        put(EquipmentSlot.FEET, UUID.fromString("6d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1d"));
    }};
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
                player.displayClientMessage(Component.literal("锻造经验 +" + expGain).withStyle(ChatFormatting.GREEN), true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        // 检查实体是否为玩家
        if (entity instanceof Player player) {
            // 获取装备槽位
            EquipmentSlot slot = event.getSlot();

            // 处理攻击速度修饰器
            if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
                ItemStack to = event.getTo();

                // 处理攻击速度修饰器
                AttributeInstance attackSpeedAttribute = Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_SPEED));
                attackSpeedAttribute.removeModifier(ATTACK_SPEED_MODIFIER_ID);

                if (to.getTag() != null && to.getItem() instanceof SwordItem && to.hasTag() && to.getTag().contains(SmithingNBTManager.ATTACK_SPEED)) {
                    float additionalAttackSpeed = to.getTag().getFloat(SmithingNBTManager.ATTACK_SPEED);
                    AttributeModifier modifier = new AttributeModifier(
                            ATTACK_SPEED_MODIFIER_ID,
                            "AttackSpeedModifier",
                            additionalAttackSpeed,
                            AttributeModifier.Operation.ADDITION
                    );
                    attackSpeedAttribute.addPermanentModifier(modifier);
                }
            }

            // 处理盔甲和盔甲韧性修饰器
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack armorStack = event.getTo();
                // 移除之前的盔甲和盔甲韧性修饰器
                removeArmorModifiers(player);

                // 检查并添加盔甲修饰器
                if (armorStack.hasTag() && armorStack.getTag().contains(SmithingNBTManager.ARMOR)) {
                    float additionalArmor = armorStack.getTag().getFloat(SmithingNBTManager.ARMOR);
                    AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);
                    if (armorAttribute != null) {
                        UUID armorModifierId = UUID.fromString("7f3b3b5a-1e8f-4a1c-9e5c-3b1c9f3b5a1e");
                        AttributeModifier armorModifier = new AttributeModifier(
                                armorModifierId,
                                "SmithingArmorModifier",
                                additionalArmor,
                                AttributeModifier.Operation.ADDITION
                        );
                        armorAttribute.addPermanentModifier(armorModifier);
                    }
                }
                // 检查并添加盔甲韧性修饰器
                if (armorStack.hasTag() && armorStack.getTag().contains(SmithingNBTManager.ARMOR_TOUGHNESS)) {
                    float additionalArmorToughness = armorStack.getTag().getFloat(SmithingNBTManager.ARMOR_TOUGHNESS);
                    AttributeInstance armorToughnessAttribute = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
                    if (armorToughnessAttribute != null) {
                        UUID armorToughnessModifierId = UUID.fromString("8f4c4c6b-2f9f-5b2d-ad5c-4d2c8f4c6b2f");
                        AttributeModifier armorToughnessModifier = new AttributeModifier(
                                armorToughnessModifierId,
                                "SmithingArmorToughnessModifier",
                                additionalArmorToughness,
                                AttributeModifier.Operation.ADDITION
                        );
                        armorToughnessAttribute.addPermanentModifier(armorToughnessModifier);
                    }
                }

                // 添加最大生命值修饰器
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    AttributeInstance maxHealthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
                    if (maxHealthAttribute != null) {
                        // 移除该槽位的旧修饰器
                        UUID modifierId = MAX_HEALTH_MODIFIER_IDS.get(slot);
                        maxHealthAttribute.removeModifier(modifierId);
                        
                        // 检查新装备是否有最大生命值加成
                        ItemStack newArmor = event.getTo();
                        if (!newArmor.isEmpty() && newArmor.hasTag() && newArmor.getTag().contains(SmithingNBTManager.MAX_HEALTH)) {
                            float additionalMaxHealth = newArmor.getTag().getFloat(SmithingNBTManager.MAX_HEALTH);
                            AttributeModifier maxHealthModifier = new AttributeModifier(
                                    modifierId,
                                    "SmithingMaxHealth." + slot.getName(),
                                    additionalMaxHealth,
                                    AttributeModifier.Operation.ADDITION
                            );
                            maxHealthAttribute.addPermanentModifier(maxHealthModifier);
                        }
                    }
                }

                // 处理移动速度修饰器
                if (armorStack.hasTag() && armorStack.getTag().contains(SmithingNBTManager.MOVE_SPEED)) {
                    float additionalMoveSpeed = armorStack.getTag().getFloat(SmithingNBTManager.MOVE_SPEED);
                    AttributeInstance moveSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
                    if (moveSpeedAttribute != null) {
                        UUID moveSpeedModifierId = UUID.fromString("5a3b1c2d-6f8e-4d9c-b2a1-7f5c3d2b1a6e");
                        AttributeModifier moveSpeedModifier = new AttributeModifier(
                                moveSpeedModifierId,
                                "SmithingMoveSpeedModifier",
                                additionalMoveSpeed / 100.0f,
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                        );
                        moveSpeedAttribute.addPermanentModifier(moveSpeedModifier);
                    }
                }
            }
        }
    }

    // 辅助方法：移除盔甲和其他修饰器
    private static void removeArmorModifiers(Player player) {
        UUID[] modifierIds = {
            UUID.fromString("7f3b3b5a-1e8f-4a1c-9e5c-3b1c9f3b5a1e"),   // 盔甲修饰器
            UUID.fromString("8f4c4c6b-2f9f-5b2d-ad5c-4d2c8f4c6b2f"),   // 盔甲韧性修饰器
            UUID.fromString("9d5c3b1a-2f8e-4d6c-a1b3-5c7f2d9e3b1a"),   // 最大生命值修饰器
            UUID.fromString("5a3b1c2d-6f8e-4d9c-b2a1-7f5c3d2b1a6e"),    // 移动速度修饰器
            
        };
        
        AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);
        AttributeInstance armorToughnessAttribute = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        AttributeInstance maxHealthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance moveSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        //AttributeInstance absorptionAttribute = player.getAttribute(Attributes.MAX_ABSORPTION);

        if (armorAttribute != null) {
            for (UUID modifierId : modifierIds) {
                armorAttribute.removeModifier(modifierId);
            }
        }
        
        if (armorToughnessAttribute != null) {
            for (UUID modifierId : modifierIds) {
                armorToughnessAttribute.removeModifier(modifierId);
            }
        }
        
        if (maxHealthAttribute != null) {
            for (UUID modifierId : modifierIds) {
                maxHealthAttribute.removeModifier(modifierId);
            }
        }
        
        if (moveSpeedAttribute != null) {
            for (UUID modifierId : modifierIds) {
                moveSpeedAttribute.removeModifier(modifierId);
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
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 只处理玩家 Tick 阶段为 END 的情况
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
        
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
                // 设置5分钟的吸收心效果
                int absorptionDuration = 20 * 60 * 5; // 5分钟，以tick为单位
                int amplifier = Math.min((int)(totalAbsorption / 2), 4); // 等级限制在0-4之间
                
                // 如果已经有吸收效果，选择较高的等级
                MobEffectInstance currentEffect = player.getEffect(MobEffects.ABSORPTION);
                if (currentEffect != null) {
                    amplifier = Math.max(currentEffect.getAmplifier(), amplifier);
                }
                
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, absorptionDuration, amplifier, false, false));
                
                // 显示转换提示
                player.displayClientMessage(Component.literal("吸收值转换为吸收心效果，持续5分钟").withStyle(ChatFormatting.GOLD), true);
                
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
                    player.displayClientMessage(Component.literal("装备获得吸收值：" + String.format("%.1f", newAbsorption)).withStyle(ChatFormatting.GOLD), true);
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
            if (tag.contains(SmithingNBTManager.ATTACK_DAMAGE)) {
                tooltips.add(Component.literal("攻击力+" + itemStack.getOrCreateTag().getInt(SmithingNBTManager.ATTACK_DAMAGE))
                        .withStyle(ChatFormatting.BLUE));
            }
            if (tag.contains(SmithingNBTManager.ATTACK_SPEED)) {
                tooltips.add(Component.literal("攻击速度+" + itemStack.getOrCreateTag().getInt(SmithingNBTManager.ATTACK_SPEED))
                        .withStyle(ChatFormatting.BLUE));
            }
            if (tag.contains(SmithingNBTManager.DURABILITY)) {
                tooltips.add(Component.literal("额外耐久：" + itemStack.getOrCreateTag().getInt(SmithingNBTManager.DURABILITY))
                        .withStyle(ChatFormatting.BLUE));
            }
            if (tag.contains(SmithingNBTManager.ARMOR)) {
                tooltips.add(Component.literal("盔甲：" + itemStack.getOrCreateTag().getInt(SmithingNBTManager.ARMOR))
                        .withStyle(ChatFormatting.BLUE));
            }
            if (tag.contains(SmithingNBTManager.ARMOR_TOUGHNESS)) {
                tooltips.add(Component.literal("盔甲韧性：" + itemStack.getOrCreateTag().getInt(SmithingNBTManager.ARMOR_TOUGHNESS))
                        .withStyle(ChatFormatting.BLUE));
            }
            if (tag.contains(SmithingNBTManager.BREAK_SPEED)) {
                tooltips.add(Component.literal("挖掘速度：" + itemStack.getOrCreateTag().getInt(SmithingNBTManager.BREAK_SPEED))
                        .withStyle(ChatFormatting.BLUE));
            }
            if (tag.contains(SmithingNBTManager.KNOCKBACK_RESISTANCE)) {
                tooltips.add(Component.literal("抗击退：" + itemStack.getOrCreateTag().getInt(SmithingNBTManager.KNOCKBACK_RESISTANCE))
                        .withStyle(ChatFormatting.BLUE));
            }
            if (tag.contains(SmithingNBTManager.CRITICAL_STRIKE_CHANCE)) {
                tooltips.add(Component.literal("暴击率：" + itemStack.getOrCreateTag().getFloat(SmithingNBTManager.CRITICAL_STRIKE_CHANCE) + "%")
                        .withStyle(ChatFormatting.RED));
            }
            if (tag.contains(SmithingNBTManager.MAX_HEALTH)) {
                tooltips.add(Component.literal("生命值增加：+" + itemStack.getOrCreateTag().getInt(SmithingNBTManager.MAX_HEALTH))
                        .withStyle(ChatFormatting.GREEN));
            }
            if (tag.contains(SmithingNBTManager.MOVE_SPEED)) {
                tooltips.add(Component.literal("移动速度：" + itemStack.getOrCreateTag().getFloat(SmithingNBTManager.MOVE_SPEED) + "%")
                        .withStyle(ChatFormatting.AQUA));
            }
            if (tag.contains(SmithingNBTManager.ABSORPTION)) {
                tooltips.add(Component.literal("吸收值：" + itemStack.getOrCreateTag().getFloat(SmithingNBTManager.ABSORPTION))
                        .withStyle(ChatFormatting.GOLD));
            }
            if (tag.contains(SmithingNBTManager.THORNS)) {
                tooltips.add(Component.literal("荆棘：" + itemStack.getOrCreateTag().getFloat(SmithingNBTManager.THORNS) + "%伤害反弹")
                        .withStyle(ChatFormatting.RED));
            }
        }
       
    }

    private static final Map<UUID, Float> playerAbsorptionValues = new HashMap<>();
    private static final Map<UUID, Long> playerLastAbsorptionTime = new HashMap<>();
    private static final long ABSORPTION_CONVERSION_DELAY = 20 * 5; // 5秒后转换
    private static final float ABSORPTION_CONVERSION_RATIO = 0.5f; // 50%转换为吸收心

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
