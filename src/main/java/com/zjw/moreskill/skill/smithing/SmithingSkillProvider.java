package com.zjw.moreskill.skill.smithing;

import com.zjw.moreskill.skill.SkillCapabilityProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class SmithingSkillProvider extends SkillCapabilityProvider<Smithing> {

    public static final Capability<Smithing> SMITHING_SKILL = CapabilityManager.get(new CapabilityToken<>() {});

    public SmithingSkillProvider() {
        super(new Smithing());
    }

    @Override
    protected Capability<Smithing> getCapabilityType() {
        return SMITHING_SKILL;
    }
}