package com.zjw.moreskill.skill.farming;

import com.zjw.moreskill.skill.SkillCapabilityProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class FarmingProvider extends SkillCapabilityProvider<Farming> {

    public static final Capability<Farming> FARMING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public FarmingProvider() {
        super(new Farming());
    }

    @Override
    protected Capability<Farming> getCapabilityType() {
        return FARMING_CAPABILITY;
    }
}