package com.zjw.moreskill.skill.cooking;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.zjw.moreskill.skill.farming.Farming;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class CookingProvider implements ICapabilityProvider {
     public static final Capability<Cooking> COOKING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private static Cooking cooking = new Cooking();
    private final LazyOptional<Cooking> optional = LazyOptional.of(() -> cooking);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == COOKING_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }
}
