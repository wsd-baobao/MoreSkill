package com.zjw.moreskill.skill.mining;


import com.zjw.moreskill.MoreSkill;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = MoreSkill.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
                List<ItemStack> drops = state.getDrops(
                        new LootParams.Builder((ServerLevel) player.level())
                                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                                .withParameter(LootContextParams.TOOL, player.getMainHandItem()));
                //根据挖掘等级随机向drops中添加N个掉落物
                for (int i = 0; i < mining.getItemsCountByLevel(); i++) {
                    drops.add(drops.get(0));
                }
                for (int i = 1; i < drops.size(); i++) {
                    ItemStack drop = drops.get(i);
                    if (!drop.isEmpty()) {
                        ItemEntity itemEntity = new ItemEntity(player.level(), pos.getX(), pos.getY() + 1, pos.getZ(), drop);
                        itemEntity.setUnlimitedLifetime();
                        world.addFreshEntity(itemEntity);
                    }
                }
                System.out.println("获取mining经验");
                mining.addExp(player, drops.size());
                player.giveExperiencePoints(drops.size());

            } else if (MiningManager.isStone(state)) {
                mining.addExp(player, 1);
            }
        });
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.level().getGameTime() % 200 == 0) {
            player.getCapability(MiningSkillProvider.MINING_SKILL).ifPresent(mining -> {
                if (player.position().y < 60) {
                    MiningManager.addEffect(player, mining.getLevel());
                }
            });
        }

    }

}
