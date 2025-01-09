package com.zjw.moreskill.skill.fishing;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FishingSkillProvider implements ICapabilityProvider {

    public static final Capability<Fishing> FISHING_SKILL = CapabilityManager.get(new CapabilityToken<>() {
    });
    private static Fishing fishing = new Fishing();
    private final LazyOptional<Fishing> fishingCapability = LazyOptional.of(() -> fishing);

    public FishingSkillProvider() {
        // 初始化代码...
    }
    /**
     * 获取指定能力的实现
     * 此方法用于注册和获取能力（Capability）的实例，在Minecraft中用于实现自定义的能力系统
     * 当能力与当前对象关联时，返回一个包含能力实例的LazyOptional对象；否则，返回一个空的LazyOptional对象
     *
     * @param cap  要获取的能力类型
     * @param side 方向，表示能力的来源方向，可能为null
     * @param <T>  能力的具体类型，由调用方指定
     * @return 如果当前对象具有指定的能力，则返回包含该能力实例的LazyOptional对象；否则返回空的LazyOptional对象
     */
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // 检查传入的能力是否为FISHING_SKILL能力
        return cap == FISHING_SKILL ? fishingCapability.cast() : LazyOptional.empty();
    }


}
