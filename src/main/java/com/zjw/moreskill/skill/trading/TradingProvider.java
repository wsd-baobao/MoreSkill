package com.zjw.moreskill.skill.trading;

import com.zjw.moreskill.skill.SkillCapabilityProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class TradingProvider extends SkillCapabilityProvider<Trading> {

    public static final Capability<Trading> TRADING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public TradingProvider() {
        super(new Trading());
    }

    @Override
    protected Capability<Trading> getCapabilityType() {
        return TRADING_CAPABILITY;
    }
}