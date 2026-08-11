package com.zjw.moreskill.attribute;

public final class AttributeEffects {

    private AttributeEffects() {}

    public static double getAttackDamageBonus(int strengthPoints) {
        return strengthPoints * 0.5;
    }

    public static double getSpeedBonus(int agilityPoints) {
        return agilityPoints * 0.002;
    }

    public static double getMiningSpeedBonus(int agilityPoints) {
        return agilityPoints * 0.01;
    }

    public static double getPotionDurationMultiplier(int intelligencePoints) {
        return 1.0 + intelligencePoints * 0.01;
    }

    public static double getMaxHealthBonus(int vitalityPoints) {
        return vitalityPoints * 0.5;
    }

    public static double getLuckBonus(int luckPoints) {
        return luckPoints * 0.1;
    }
}
