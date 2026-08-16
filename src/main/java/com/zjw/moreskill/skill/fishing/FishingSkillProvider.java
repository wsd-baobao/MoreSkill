package com.zjw.moreskill.skill.fishing;

import com.zjw.moreskill.skill.SkillCapabilityProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class FishingSkillProvider extends SkillCapabilityProvider<Fishing> {

    public static final Capability<Fishing> FISHING_SKILL = CapabilityManager.get(new CapabilityToken<>() {});

    public FishingSkillProvider() {
        super(new Fishing());
    }

    @Override
    protected Capability<Fishing> getCapabilityType() {
        return FISHING_SKILL;
    }
}