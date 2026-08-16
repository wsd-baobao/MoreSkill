package com.zjw.moreskill.skill.mining;


import com.zjw.moreskill.MoreSkill;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class MiningHandler {

    @SubscribeEvent
    public void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
        //破坏所有的方块都会执行
        Player player = event.getPlayer();
        player.getCapability(MiningSkillProvider.MINING_SKILL).ifPresent(mining -> {
            BlockState state = event.getState();
            BlockPos pos = event.getPos();
            Level world = player.level();
            if (MiningManager.isOre(state)) {
                if (!(world instanceof ServerLevel serverLevel)) {
                    return;
                }
                List<ItemStack> drops = state.getDrops(
                        new LootParams.Builder(serverLevel)
                                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                                .withParameter(LootContextParams.TOOL, player.getMainHandItem()));
                // 原版会正常生成完整战利品表，这里仅根据挖掘等级生成额外掉落副本
                if (!drops.isEmpty()) {
                    ItemStack baseDrop = drops.get(0);
                    for (int i = 0; i < mining.getItemsCountByLevel(); i++) {
                        if (!baseDrop.isEmpty()) {
                            ItemEntity itemEntity = new ItemEntity(world,
                                    pos.getX(), pos.getY() + 1, pos.getZ(), baseDrop.copy());
                            itemEntity.setDefaultPickUpDelay();
                            world.addFreshEntity(itemEntity);
                        }
                    }
                }
                mining.addExp(player, drops.size());
                player.giveExperiencePoints(drops.size());
            } else if (MiningManager.isStone(state)) {
                mining.addExp(player, 1);
            }
        });
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        Player player = event.player;
        if (player.level().getGameTime() % 200 == 0) {//10秒
            player.getCapability(MiningSkillProvider.MINING_SKILL).ifPresent(mining -> {
                if (player.position().y < 60) {
                    MiningManager.addEffect(player, mining.getLevel());
                }
            });
        }
    }

    @SubscribeEvent
    public void onBreakSpeedForPlayerLevel(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        player.getCapability(MiningSkillProvider.MINING_SKILL).ifPresent(mining -> {
            event.setNewSpeed(event.getNewSpeed() + mining.getLevel() * 0.02f);
            //挖基岩逻辑
//            if (mining.getLevel()>=100){
//                if (event.getState().is(Blocks.BEDROCK)){
//                    BlockPos pos = event.getEntity().blockPosition();
//                    BlockState randomOre = MiningManager.getRandomOre();
//                    List<ItemStack> drops = randomOre.getDrops(
//                            new LootParams.Builder((ServerLevel) player.level())
//                                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
//                                    .withParameter(LootContextParams.TOOL, player.getMainHandItem()));
//                    if (!drops.isEmpty()) {
//                        ItemEntity itemEntity = new ItemEntity(player.level(), pos.getX(), pos.getY() + 1, pos.getZ(), drops.get(0));
//                        itemEntity.setUnlimitedLifetime();
//                        player.level().addFreshEntity(itemEntity);
//                    }
//                }
//            }
        });

    }

}
