package com.zjw.moreskill.utils;

import net.minecraft.world.item.ItemStack;

public class ItemNBTUtil {

    public static ItemStack setNBT(ItemStack itemStack, String key, int value) {
        itemStack.getOrCreateTag().putInt(key, value);
        return itemStack;
    }

    public static ItemStack setAuthorNBT(ItemStack itemStack, String key, String value) {
        itemStack.getOrCreateTag().putString(key, value);
        return itemStack;
    }
}
