package com.zjw.moreskill.attribute;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.EnumMap;
import java.util.Map;

public class AttributeData implements INBTSerializable<CompoundTag> {
    private static final int POINT_BASE_COST = 50;
    private static final int POINT_COST_SCALING = 25;

    private int availablePoints;
    private int totalPointsBought;
    private final Map<ModAttribute, Integer> points = new EnumMap<>(ModAttribute.class);

    public AttributeData() {
        this.availablePoints = 0;
        this.totalPointsBought = 0;
        for (ModAttribute attr : ModAttribute.values()) {
            points.put(attr, 0);
        }
    }

    public int getAvailablePoints() {
        return availablePoints;
    }

    public int getTotalPointsBought() {
        return totalPointsBought;
    }

    public int getPoints(ModAttribute attribute) {
        return points.getOrDefault(attribute, 0);
    }

    public int getCostForNextPoint() {
        return POINT_BASE_COST + totalPointsBought * POINT_COST_SCALING;
    }

    public int getXpForBuyAmount(int amount) {
        int total = 0;
        for (int i = 0; i < amount; i++) {
            total += POINT_BASE_COST + (totalPointsBought + i) * POINT_COST_SCALING;
        }
        return total;
    }

    public boolean buyPoints(int xpAmount) {
        if (xpAmount <= 0) return false;
        int cost = getCostForNextPoint();
        if (xpAmount < cost) return false;
        xpAmount -= cost;
        availablePoints++;
        totalPointsBought++;
        while (xpAmount > 0) {
            int nextCost = getCostForNextPoint();
            if (xpAmount < nextCost) break;
            xpAmount -= nextCost;
            availablePoints++;
            totalPointsBought++;
        }
        return true;
    }

    public boolean allocate(ModAttribute attribute) {
        if (availablePoints <= 0) return false;
        int current = getPoints(attribute);
        if (current >= attribute.getMaxPoints()) return false;
        points.put(attribute, current + 1);
        availablePoints--;
        return true;
    }

    public int getTotalAllocated() {
        return points.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("AvailablePoints", availablePoints);
        tag.putInt("TotalBought", totalPointsBought);
        for (Map.Entry<ModAttribute, Integer> entry : points.entrySet()) {
            tag.putInt("attr_" + entry.getKey().getId(), entry.getValue());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        availablePoints = nbt.getInt("AvailablePoints");
        totalPointsBought = nbt.getInt("TotalBought");
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
