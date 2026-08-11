package com.zjw.moreskill.attribute;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttributeProvider implements ICapabilityProvider {
    public static final Capability<AttributeData> ATTRIBUTE_CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});

    private final AttributeData data = new AttributeData();
    private final LazyOptional<AttributeData> optional = LazyOptional.of(() -> data);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ATTRIBUTE_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }
}
