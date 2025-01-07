package com.zjw.moreskill.item;

import com.zjw.moreskill.skill.smithing.Smithing;
import com.zjw.moreskill.skill.smithing.SmithingNBTManager;
import com.zjw.moreskill.skill.smithing.SmithingSkillProvider;
import com.zjw.moreskill.utils.ItemNBTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class ForgingHammerItem extends Item {

    public ForgingHammerItem() {
        super(new Properties());

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (level.isClientSide()) {
            return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
        }
        if (player.getUseItemRemainingTicks() > 0) {
            return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
        }
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offhandItem = player.getOffhandItem();

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        if (offhandItem.isEmpty()) {
            return InteractionResultHolder.success(mainHandItem);
        }
        String playerName = player.getName().getString();

        // 获取玩家的锻造技能
        Smithing smithing = player.getCapability(SmithingSkillProvider.SMITHING_SKILL).orElse(null);
        if (smithing == null || smithing.getLevel() >= Smithing.MAX_LEVEL) {
            return InteractionResultHolder.success(mainHandItem);
        }

        if (!offhandItem.getOrCreateTag().contains(SmithingNBTManager.AUTHOR)) {
            int expGain = 0;
            int smithinglevel = smithing.getLevel();
            
            if (offhandItem.getItem() instanceof SwordItem || 
            offhandItem.getItem() instanceof TridentItem) {
                // 攻击伤害：等级/3（最高33）
                setNBT(offhandItem, SmithingNBTManager.ATTACK_DAMAGE, smithinglevel/3);
                // 攻击速度：等级/10（最高10）
                setNBT(offhandItem, SmithingNBTManager.ATTACK_SPEED, smithinglevel/10);
                // 耐久度：等级*300（最高30000）
                setNBT(offhandItem, SmithingNBTManager.DURABILITY, smithinglevel*300);
                // 修复效率：等级*300（与耐久相同）
                setNBT(offhandItem, SmithingNBTManager.MAX_MENDING, smithinglevel*300);
                // 暴击率：等级/4（最高25）
                setNBT(offhandItem, SmithingNBTManager.CRITICAL_STRIKE_CHANCE, smithinglevel/4);
                setAuthorNBT(offhandItem, SmithingNBTManager.AUTHOR, playerName);
                expGain = 20;
            }
            if (offhandItem.getItem() instanceof DiggerItem) {
                // 耐久度：等级*300
                setNBT(offhandItem, SmithingNBTManager.DURABILITY, smithinglevel*300);
                // 挖掘速度：等级/5（最高20）
                setNBT(offhandItem, SmithingNBTManager.BREAK_SPEED, smithinglevel/5);
                // 修复效率：等级*300（与耐久相同）
                setNBT(offhandItem, SmithingNBTManager.MAX_MENDING, smithinglevel*300);
                setAuthorNBT(offhandItem, SmithingNBTManager.AUTHOR, playerName);
                expGain = 15;
            }
            if (offhandItem.getItem() instanceof ArmorItem) {
                // 护甲值：等级/4（最高25）
                setNBT(offhandItem, SmithingNBTManager.ARMOR, smithinglevel/4);
                // 护甲韧性：等级/8（最高12）
                setNBT(offhandItem, SmithingNBTManager.ARMOR_TOUGHNESS, smithinglevel/8);
                // 击退抗性：等级/20（最高5）
                setNBT(offhandItem, SmithingNBTManager.KNOCKBACK_RESISTANCE, smithinglevel/20);
                // 生命值加成：等级/10（最高10）
                setNBT(offhandItem, SmithingNBTManager.MAX_HEALTH, smithinglevel/10);
                // 吸收值：从0开始累积
                setNBT(offhandItem, SmithingNBTManager.ABSORPTION, 0);
                // 荆棘：等级/10（最高10）
                setNBT(offhandItem, SmithingNBTManager.THORNS, smithinglevel/10);
                // 耐久度：等级*300（最高30000）
                setNBT(offhandItem, SmithingNBTManager.DURABILITY, smithinglevel*300);
                // 修复效率：等级*300（与耐久相同）
                setNBT(offhandItem, SmithingNBTManager.MAX_MENDING, smithinglevel*300);
                setAuthorNBT(offhandItem, SmithingNBTManager.AUTHOR, playerName);
                expGain = 25;
            }
            if (offhandItem.getItem() instanceof BowItem || 
                offhandItem.getItem() instanceof CrossbowItem ) {
                setNBT(offhandItem, SmithingNBTManager.ATTACK_DAMAGE, smithinglevel/3);
                // 远程武器属性
                // 耐久度：等级*300
                setNBT(offhandItem, SmithingNBTManager.DURABILITY, smithinglevel*300);
                // 修复效率：等级*300（与耐久相同）
                setNBT(offhandItem, SmithingNBTManager.MAX_MENDING, smithinglevel*300);
                setAuthorNBT(offhandItem, SmithingNBTManager.AUTHOR, playerName);
                expGain = 15;
            }

            // 增加经验并通知玩家
            if (expGain > 0) {
                smithing.addExp(expGain);
                player.displayClientMessage(Component.literal("锻造等级: " + smithinglevel + 
                    " (+" + expGain + "经验)").withStyle(ChatFormatting.GREEN), true);
            }
            return InteractionResultHolder.success(mainHandItem);
        }
        return InteractionResultHolder.success(mainHandItem);
    }

    private void setNBT(ItemStack itemStack, String key, int value) {
        ItemNBTUtil.setNBT(itemStack, key, value);
    }

    private void setAuthorNBT(ItemStack itemStack, String key, String value) {
        ItemNBTUtil.setAuthorNBT(itemStack, key, value);
    }
}
