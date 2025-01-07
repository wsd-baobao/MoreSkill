package com.zjw.moreskill.skill.alchemy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class AlchemyProvider implements ICapabilityProvider {
    public static final Capability<Alchemy> ALCHEMY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private static Alchemy alchemy = new Alchemy();
    private final LazyOptional<Alchemy> optional = LazyOptional.of(() -> alchemy);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ALCHEMY_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }
}
