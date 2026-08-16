package com.zjw.moreskill.skill.cooking;

import com.zjw.moreskill.skill.SkillCapabilityProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CookingProvider extends SkillCapabilityProvider<Cooking> {

    public static final Capability<Cooking> COOKING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public CookingProvider() {
        super(new Cooking());
    }

    @Override
    protected Capability<Cooking> getCapabilityType() {
        return COOKING_CAPABILITY;
    }
}