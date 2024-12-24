package com.zjw.moreskill.skill.mining;

import com.zjw.moreskill.Config;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class MiningManager {

    // 使用静态块初始化矿石类型的集合
    private static final Set<Block> ORE_TYPES = new HashSet<>();
    private static final Set<Block> STONE_TYPES = new HashSet<>();

    public static void addEffect(Player player, int level) {
        if (level > 10) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20 * 30, 0,true, true));
        }
        if (level >20){
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20 * 30, 0,true, true));
        }
        if (level >40){
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 30, 0,true, true));
        }
        if (level >50){
            player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 20 * 30, 0,true, true));
        }
        if (level >80){
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 30, 0,true, true));
        }
        if (level >=100){
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 20 * 30, 0,true, true));
        }
    }
    //挖基岩用的
    public static BlockState getRandomOre() {
        Random rand = new Random();
        int index = rand.nextInt(ORE_TYPES.size());  // 获取一个随机索引
        Iterator<Block> iterator = ORE_TYPES.iterator();
        // 跳过随机索引的元素
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next().defaultBlockState();
    }
    public static boolean isOre(BlockState block) {
        return ORE_TYPES.contains(block.getBlock());
    }

    public static boolean isStone(BlockState block) {
        return STONE_TYPES.contains(block.getBlock());
    }

    private static void addOreForConfig() {
        ORE_TYPES.addAll(Config.miningOre);
    }
    static {
        ORE_TYPES.add(Blocks.COAL_ORE);
        ORE_TYPES.add(Blocks.IRON_ORE);
        ORE_TYPES.add(Blocks.GOLD_ORE);
        ORE_TYPES.add(Blocks.REDSTONE_ORE);
        ORE_TYPES.add(Blocks.DIAMOND_ORE);
        ORE_TYPES.add(Blocks.EMERALD_ORE);
        ORE_TYPES.add(Blocks.LAPIS_ORE);
        ORE_TYPES.add(Blocks.NETHER_GOLD_ORE);
        ORE_TYPES.add(Blocks.NETHER_QUARTZ_ORE);
        ORE_TYPES.add(Blocks.ANCIENT_DEBRIS);
        ORE_TYPES.add(Blocks.COPPER_ORE);
        ORE_TYPES.add(Blocks.DEEPSLATE_COAL_ORE);
        ORE_TYPES.add(Blocks.DEEPSLATE_IRON_ORE);
        ORE_TYPES.add(Blocks.DEEPSLATE_GOLD_ORE);
        ORE_TYPES.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        ORE_TYPES.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        ORE_TYPES.add(Blocks.DEEPSLATE_EMERALD_ORE);
        ORE_TYPES.add(Blocks.DEEPSLATE_LAPIS_ORE);
        ORE_TYPES.add(Blocks.DEEPSLATE_COPPER_ORE);
        addOreForConfig();

        STONE_TYPES.add(Blocks.STONE);
        STONE_TYPES.add(Blocks.GRANITE);
        STONE_TYPES.add(Blocks.POLISHED_GRANITE);
        STONE_TYPES.add(Blocks.DIORITE);
        STONE_TYPES.add(Blocks.POLISHED_DIORITE);
        STONE_TYPES.add(Blocks.ANDESITE);
        STONE_TYPES.add(Blocks.POLISHED_ANDESITE);
        STONE_TYPES.add(Blocks.DEEPSLATE);
        STONE_TYPES.add(Blocks.COBBLESTONE);
        STONE_TYPES.add(Blocks.BLACKSTONE);
        STONE_TYPES.add(Blocks.POLISHED_BLACKSTONE);
    }



}
