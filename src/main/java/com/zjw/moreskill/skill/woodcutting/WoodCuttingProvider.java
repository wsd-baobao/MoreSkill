package com.zjw.moreskill.skill.woodcutting;

import com.zjw.moreskill.skill.SkillCapabilityProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class WoodCuttingProvider extends SkillCapabilityProvider<WoodCutting> {

    public static final Capability<WoodCutting> WOODCUTTING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public WoodCuttingProvider() {
        super(new WoodCutting());
    }

    @Override
    protected Capability<WoodCutting> getCapabilityType() {
        return WOODCUTTING_CAPABILITY;
    }
}