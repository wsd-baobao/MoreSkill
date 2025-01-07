package com.zjw.moreskill.skill.woodcutting;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.zjw.moreskill.skill.smithing.Smithing;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class WoodCuttingProvider implements ICapabilityProvider {

    public static final Capability<WoodCutting> WOODCUTTING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private static WoodCutting woodCutting = new WoodCutting();
    private final LazyOptional<WoodCutting> instance = LazyOptional.of(() -> woodCutting);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == WOODCUTTING_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

}
