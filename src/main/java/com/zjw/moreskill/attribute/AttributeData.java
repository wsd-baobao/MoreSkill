package com.zjw.moreskill.attribute;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.EnumMap;
import java.util.Map;

public class AttributeData implements INBTSerializable<CompoundTag> {
    private static final int BASE_COST = 5;
    private static final int SCALING_FACTOR = 1;

    private final Map<ModAttribute, Integer> points = new EnumMap<>(ModAttribute.class);

    public AttributeData() {
        for (ModAttribute attr : ModAttribute.values()) {
            points.put(attr, 0);
        }
    }

    public int getPoints(ModAttribute attribute) {
        return points.getOrDefault(attribute, 0);
    }

    public boolean allocate(ModAttribute attribute) {
        int current = getPoints(attribute);
        if (current >= attribute.getMaxPoints()) {
            return false;
        }
        points.put(attribute, current + 1);
        return true;
    }

    public int getCostForNextPoint(ModAttribute attribute) {
        int current = getPoints(attribute);
        return BASE_COST + current * SCALING_FACTOR;
    }

    public int getTotalSpent() {
        return points.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<ModAttribute, Integer> entry : points.entrySet()) {
            tag.putInt("attr_" + entry.getKey().getId(), entry.getValue());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (ModAttribute attr : ModAttribute.values()) {
            String key = "attr_" + attr.getId();
            if (nbt.contains(key)) {
                int value = Math.min(nbt.getInt(key), attr.getMaxPoints());
                points.put(attr, Math.max(value, 0));
            } else {
                points.put(attr, 0);
            }
        }
    }
}
