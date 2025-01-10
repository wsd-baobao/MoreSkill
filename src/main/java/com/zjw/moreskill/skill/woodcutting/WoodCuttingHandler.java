package com.zjw.moreskill.skill.woodcutting;

import com.zjw.moreskill.MoreSkill;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WoodCuttingHandler {

    TagKey<Block> logsTag = BlockTags.create(new ResourceLocation("minecraft", "logs"));

    //检测破坏的方块是否是原木
    @SubscribeEvent
    public void onPlayerBreakBlock(BlockEvent.BreakEvent event){
        //破坏所有的方块都会执行
        Player player = event.getPlayer();
        player.getCapability(WoodCuttingProvider.WOODCUTTING_CAPABILITY).ifPresent(woodcutting -> {
            BlockState state = event.getState();
            BlockPos pos = event.getPos();
            Level world = player.level();
            if (isLogBlock(state.getBlock())){
                // 根据等级增加经验，经验数量为等级/10
                int expGain = woodcutting.getLevel() / 10;
                woodcutting.addExp(expGain);
                player.giveExperiencePoints(expGain);
                // 计算并生成额外掉落物
                int extraDropCount = calculateExtraDrops(woodcutting.getLevel(), new Random());
                for (int i = 0; i < extraDropCount; i++) {
                    ItemStack extraDrop = new ItemStack(state.getBlock().asItem());
                    ItemEntity itemEntity = new ItemEntity(world, 
                        pos.getX() + 0.5, 
                        pos.getY() + 0.5, 
                        pos.getZ() + 0.5, 
                        extraDrop);
                    itemEntity.setDefaultPickUpDelay();
                    world.addFreshEntity(itemEntity);
                }
            }
        });
    }
    // 使用标签检测方块是否是木头
    private boolean isLogBlock(Block block) {
        // 获取 "minecraft:logs" 标签
        TagKey<Block> logsTag = BlockTags.create(new ResourceLocation("minecraft", "logs"));

        // 检查方块是否属于该标签
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.tags()).getTag(logsTag).contains(block);
    }
    // 计算额外的掉落数量
    private int calculateExtraDrops(int woodcuttingLevel, Random random) {
        // 随机1到(等级/10)个额外掉落物
        int maxExtraDrops = woodcuttingLevel / 10;
        return maxExtraDrops > 0 ? random.nextInt(maxExtraDrops) + 1 : 0;
    }
}
