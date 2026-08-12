package com.zjw.moreskill.attribute;

public final class AttributeEffects {

    private AttributeEffects() {}

    // ===== STRENGTH =====
    public static double getAttackDamageBonus(int points) {
        return points * 0.5;
    }

    public static double getCriticalDamageBonus(int points) {
        return points * 0.02;
    }

    public static double getArmorBonus(int points) {
        return points * 0.2;
    }

    // ===== AGILITY =====
    public static double getSpeedBonus(int points) {
        return points * 0.0015;
    }

    public static double getCritRateBonus(int points) {
        return points * 0.002;
    }

    public static double getAttackSpeedBonus(int points) {
        return points * 0.003;
    }

    public static double getDodgeChance(int points) {
        return points * 0.0025;
    }

    // ===== INTELLIGENCE =====
    public static double getPotionDurationMultiplier(int points) {
        return 1.0 + points * 0.01;
    }

    // ===== VITALITY =====
    public static double getArmorToughnessBonus(int points) {
        return points * 0.3;
    }

    public static double getHealthRegenBonus(int points) {
        return points * 0.005;
    }

    public static double getKnockbackResistanceBonus(int points) {
        return points * 0.005;
    }

    // ===== LUCK =====
    public static double getXpGainBonus(int points) {
        return points * 0.003;
    }

    public static double getMobDropBonus(int points) {
        return points * 0.003;
    }

    public static double getMiningDropBonus(int points) {
        return points * 0.003;
    }

    public static double getFishingBonus(int points) {
        return points * 0.003;
    }

    public static double getLuckCritBonus(int points) {
        return points * 0.002;
    }

    public static double getLuckDodgeBonus(int points) {
        return points * 0.002;
    }
}
