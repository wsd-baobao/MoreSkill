package com.zjw.moreskill.skill.farming;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FarmingProvider implements ICapabilityProvider {
    public static final Capability<Farming> FARMING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private static Farming farming = new Farming();
    private final LazyOptional<Farming> optional = LazyOptional.of(() -> farming);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == FARMING_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }


}
