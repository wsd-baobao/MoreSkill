package com.zjw.moreskill.skill;

import com.zjw.moreskill.attribute.AttributeProvider;
import com.zjw.moreskill.skill.alchemy.AlchemyProvider;
import com.zjw.moreskill.skill.combat.CombatProvider;
import com.zjw.moreskill.skill.cooking.CookingProvider;
import com.zjw.moreskill.skill.farming.FarmingProvider;
import com.zjw.moreskill.skill.fishing.FishingSkillProvider;
import com.zjw.moreskill.skill.mining.MiningSkillProvider;
import com.zjw.moreskill.skill.smithing.SmithingSkillProvider;
import com.zjw.moreskill.skill.trading.TradingProvider;
import com.zjw.moreskill.skill.woodcutting.WoodCuttingProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 技能/属性能力的唯一注册表。
 * 新增技能时只需在此处添加一条记录，加载/保存/同步/面板初始化全部由该列表驱动。
 */
public final class SkillRegistry {

    public record SkillEntry(
            Capability<? extends INBTSerializable<CompoundTag>> capability,
            String key,
            Supplier<ICapabilityProvider> providerFactory) {}

    private static final List<SkillEntry> SKILL_ENTRIES = List.of(
            new SkillEntry(FishingSkillProvider.FISHING_SKILL, "fishing_skill", FishingSkillProvider::new),
            new SkillEntry(MiningSkillProvider.MINING_SKILL, "mining_skill", MiningSkillProvider::new),
            new SkillEntry(SmithingSkillProvider.SMITHING_SKILL, "smithing_skill", SmithingSkillProvider::new),
            new SkillEntry(FarmingProvider.FARMING_CAPABILITY, "farming_skill", FarmingProvider::new),
            new SkillEntry(CookingProvider.COOKING_CAPABILITY, "cooking_skill", CookingProvider::new),
            new SkillEntry(CombatProvider.COMBAT_CAPABILITY, "combat_skill", CombatProvider::new),
            new SkillEntry(AlchemyProvider.ALCHEMY_CAPABILITY, "alchemy_skill", AlchemyProvider::new),
            new SkillEntry(TradingProvider.TRADING_CAPABILITY, "trading_skill", TradingProvider::new),
            new SkillEntry(WoodCuttingProvider.WOODCUTTING_CAPABILITY, "woodcutting_skill", WoodCuttingProvider::new),
            new SkillEntry(AttributeProvider.ATTRIBUTE_CAPABILITY, "attributes", AttributeProvider::new)
    );

    private static final Map<String, Capability<? extends INBTSerializable<CompoundTag>>> CAPABILITIES_BY_KEY =
            SKILL_ENTRIES.stream().collect(Collectors.toUnmodifiableMap(SkillEntry::key, SkillEntry::capability));

    private SkillRegistry() {}

    public static List<SkillEntry> getEntries() {
        return SKILL_ENTRIES;
    }

    public static Capability<? extends INBTSerializable<CompoundTag>> getByKey(String key) {
        return CAPABILITIES_BY_KEY.get(key);
    }
}
