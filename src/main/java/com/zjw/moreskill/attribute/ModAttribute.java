package com.zjw.moreskill.attribute;

import net.minecraft.network.chat.Component;

public enum ModAttribute {
    STRENGTH("strength", 100),
    AGILITY("agility", 100),
    INTELLIGENCE("intelligence", 100),
    VITALITY("vitality", 100),
    LUCK("luck", 100);

    private final String id;
    private final int maxPoints;

    ModAttribute(String id, int maxPoints) {
        this.id = id;
        this.maxPoints = maxPoints;
    }

    public String getId() {
        return id;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public Component getDisplayName() {
        return Component.translatable("attribute.moreskill." + id);
    }

    public Component getDescription() {
        return Component.translatable("attribute.moreskill." + id + ".desc");
    }

    public static ModAttribute fromId(String id) {
        for (ModAttribute attr : values()) {
            if (attr.id.equals(id)) {
                return attr;
            }
        }
        return null;
    }
}
