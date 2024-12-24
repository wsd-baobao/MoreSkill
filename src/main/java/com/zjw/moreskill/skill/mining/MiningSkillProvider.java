package com.zjw.moreskill.skill.mining;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiningSkillProvider implements ICapabilityProvider {

    public static final Capability<Mining> MINING_SKILL = CapabilityManager.get(new CapabilityToken<>() {});

    private static Mining mining = new Mining();
    private final LazyOptional<Mining> miningCapability =  LazyOptional.of(() -> mining);
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == MINING_SKILL ?  miningCapability.cast() : LazyOptional.empty();
    }
}
