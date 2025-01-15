package com.zjw.moreskill.skill.alchemy;

import java.util.ArrayList;
import java.util.List;

import com.zjw.moreskill.MoreSkill;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author zjw
 * @createTime 2021/12/16 15:41
 */
@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AlchemyHandler {

    @SubscribeEvent
    public static void onPotionEvent(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack itemStack = event.getItem();

            // 检查物品是否是药水
            if (itemStack.getItem() instanceof PotionItem) {
                // 获取玩家的技能等级
                int level = player.getCapability(AlchemyProvider.ALCHEMY_CAPABILITY)
                        .map(cap -> cap.getLevel())
                        .orElse(0);

                if (level > 0) {
                    // 获取药水的效果
                    List<MobEffectInstance> effects = PotionUtils.getMobEffects(itemStack);

                    // 从 NBT 数据中读取延长时间
                    int duration = itemStack.getOrCreateTag().getInt(AlchemyNBTManager.ALCHEMY_TIME);

                    // 延长药水时间并创建新的效果实例
                    List<MobEffectInstance> modifiedEffects = new ArrayList<>();
                    for (MobEffectInstance effect : effects) {
                        int newDuration = effect.getDuration() + (duration * 20 * 60); // 延长时间（单位：tick）
                        MobEffectInstance newEffect = new MobEffectInstance(
                                effect.getEffect(),
                                newDuration,
                                effect.getAmplifier(),
                                effect.isAmbient(),
                                effect.isVisible(),
                                effect.showIcon());
                        modifiedEffects.add(newEffect);
                    }

                    // 为玩家应用修改后的药水效果
                    for (MobEffectInstance effect : modifiedEffects) {
                        player.addEffect(effect);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPotionBrew(PlayerBrewedPotionEvent event) {
        ItemStack brewedStack = event.getStack();

        if (!brewedStack.getOrCreateTag().contains(AlchemyNBTManager.AUTHOR)) {
            event.getEntity().getCapability(AlchemyProvider.ALCHEMY_CAPABILITY).ifPresent(alchemy -> {
                CompoundTag tag = brewedStack.getOrCreateTag();
                tag.putString(AlchemyNBTManager.AUTHOR, event.getEntity().getName().getString());
                tag.putInt(AlchemyNBTManager.ALCHEMY_TIME, alchemy.getLevel());
                alchemy.addExp(3);
            });
        }
    }
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AreaEffectCloud cloud) {
            // 检查区域效果云是否由玩家创建
            if (cloud.getOwner() instanceof Player player) {
                // 获取药水的炼金时间标签
                Potion cloudPotion = cloud.getPotion();
                
                // 尝试从云的持久化数据中获取标签
                CompoundTag persistentData = cloud.getPersistentData();
                
                // 仅当存在炼金时间标签时才处理
                if (persistentData.contains(AlchemyNBTManager.ALCHEMY_TIME)) {

                    int duration = persistentData.getInt(AlchemyNBTManager.ALCHEMY_TIME);
                    MoreSkill.LOGGER.info("炼金时间:" + duration);
                    // 获取区域效果云的效果
                    List<MobEffectInstance> effects = cloudPotion.getEffects();

                    // 延长药水时间
                    List<MobEffectInstance> modifiedEffects = new ArrayList<>();
                    for (MobEffectInstance effect : effects) {
                        System.out.println("存在标签"+ duration);
                        int newDuration = effect.getDuration() + (duration * 20 * 60); // 每级增加 1 分钟
                        MobEffectInstance newEffect = new MobEffectInstance(
                                effect.getEffect(),
                                newDuration,
                                effect.getAmplifier(),
                                effect.isAmbient(),
                                effect.isVisible(),
                                effect.showIcon()
                        );
                        modifiedEffects.add(newEffect);
                    }

                    // 更新区域效果云的效果
                    Potion newPotion = new Potion(modifiedEffects.toArray(new MobEffectInstance[0]));
                    cloud.setPotion(newPotion);
                }
            }
        }
    }
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        // 检查是否是喷溅药水
        if (event.getProjectile() instanceof ThrownPotion potion) {
            // 获取药水的效果
            List<MobEffectInstance> effects = PotionUtils.getMobEffects(potion.getItem());

            // 获取药水的炼金时间标签
            ItemStack potionItem = potion.getItem();
            CompoundTag potionTag = potionItem.getOrCreateTag();
             
            // 仅当存在炼金时间标签时才处理
            if (potionTag.contains(AlchemyNBTManager.ALCHEMY_TIME)) {

                int duration = potionTag.getInt(AlchemyNBTManager.ALCHEMY_TIME);
                MoreSkill.LOGGER.info("炼金时间:" + duration);
                // 获取周围的生物
                List<LivingEntity> nearbyEntities = potion.level().getEntitiesOfClass(
                    LivingEntity.class, 
                    potion.getBoundingBox().inflate(3.0D)
                );

                for (LivingEntity livingTarget : nearbyEntities) {
                    for (MobEffectInstance effect : effects) {
                        int newDuration = effect.getDuration() + (duration * 20 * 60); // 每级增加 1 分钟
                      
                        MobEffectInstance newEffect = new MobEffectInstance(
                                effect.getEffect(),
                                newDuration,
                                effect.getAmplifier(),
                                effect.isAmbient(),
                                effect.isVisible(),
                                effect.showIcon()
                        );
                        livingTarget.addEffect(newEffect);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemtooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        List<Component> toolTip = event.getToolTip();
        CompoundTag itemTag = itemStack.getTag();
        
        if (itemTag != null && itemStack.getItem() instanceof PotionItem && 
            itemTag.contains(AlchemyNBTManager.ALCHEMY_TIME)) {
            int durationLevel = itemTag.getInt(AlchemyNBTManager.ALCHEMY_TIME);
            // Convert duration level to minutes
            Component durationComponent = Component.translatable(
                "moreskill.alchemy.duration_increase", 
                durationLevel
            );
            toolTip.add(durationComponent);
        }
    }
}
