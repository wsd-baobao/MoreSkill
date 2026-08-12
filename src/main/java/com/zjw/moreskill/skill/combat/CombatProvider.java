package com.zjw.moreskill.skill.combat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class CombatProvider implements ICapabilityProvider {
  public static final Capability<Combat> COMBAT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private final Combat combat = new Combat();
    private final LazyOptional<Combat> optional = LazyOptional.of(() -> combat);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == COMBAT_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }
    
}


