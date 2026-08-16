package com.zjw.moreskill.skill.mining;

import com.zjw.moreskill.skill.SkillCapabilityProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class MiningSkillProvider extends SkillCapabilityProvider<Mining> {

    public static final Capability<Mining> MINING_SKILL = CapabilityManager.get(new CapabilityToken<>() {});

    public MiningSkillProvider() {
        super(new Mining());
    }

    @Override
    protected Capability<Mining> getCapabilityType() {
        return MINING_SKILL;
    }
}