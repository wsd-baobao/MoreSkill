package com.zjw.moreskill.skill;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 技能 Capability 提供者公共基类：
 * 统一持有技能实例、LazyOptional 并实现 {@link #getCapability}，
 * 各技能子类只需定义自身 Capability 令牌（static 字段）并通过 {@link #getCapabilityType()} 返回。
 */
public abstract class SkillCapabilityProvider<T extends AbstractSkill> implements ICapabilityProvider {

    private final T skill;
    private final LazyOptional<T> instance;

    protected SkillCapabilityProvider(T skill) {
        this.skill = skill;
        this.instance = LazyOptional.of(() -> skill);
    }

    public T getSkill() {
        return skill;
    }

    protected abstract Capability<T> getCapabilityType();

    @Override
    public @NotNull <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction side) {
        return cap == getCapabilityType() ? instance.cast() : LazyOptional.empty();
    }
}