package com.zjw.moreskill.skill.trading;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class TradingProvider implements ICapabilityProvider {
public static final Capability<Trading> TRADING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private static Trading trading = new Trading();
    private final LazyOptional<Trading> instance = LazyOptional.of(() -> trading);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == TRADING_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }
   

    
}
