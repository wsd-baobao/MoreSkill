package com.zjw.moreskill.skill.combat;

import com.zjw.moreskill.skill.SkillCapabilityProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CombatProvider extends SkillCapabilityProvider<Combat> {

    public static final Capability<Combat> COMBAT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public CombatProvider() {
        super(new Combat());
    }

    @Override
    protected Capability<Combat> getCapabilityType() {
        return COMBAT_CAPABILITY;
    }
}