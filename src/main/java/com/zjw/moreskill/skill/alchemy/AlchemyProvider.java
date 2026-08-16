package com.zjw.moreskill.skill.alchemy;

import com.zjw.moreskill.skill.SkillCapabilityProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class AlchemyProvider extends SkillCapabilityProvider<Alchemy> {

    public static final Capability<Alchemy> ALCHEMY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public AlchemyProvider() {
        super(new Alchemy());
    }

    @Override
    protected Capability<Alchemy> getCapabilityType() {
        return ALCHEMY_CAPABILITY;
    }
}