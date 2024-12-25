package com.zjw.moreskill.skill.smithing;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmithingSkillProvider implements ICapabilityProvider {

    public static final Capability <Smithing> SMITHING_SKILL = CapabilityManager.get(new CapabilityToken<>() {});

    private final LazyOptional<Smithing> SmithingCapability =  LazyOptional.of((Smithing::new));

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == SMITHING_SKILL ? SmithingCapability.cast() : LazyOptional.empty();
    }
}
