package com.zjw.moreskill.skill.fishing;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;



public class FishingPoolBuilder {

        private static final List<ItemStack> items = new ArrayList<>();

        public FishingPoolBuilder addItem(Item item) {
            items.add(item.getDefaultInstance());
            return this;
        }
        public FishingPoolBuilder addItemStack(ItemStack itemStack) {
            items.add(itemStack);
            return this;
        }

        public FishingPoolBuilder addItems(Item... itemsToAdd) {
            for (Item item : itemsToAdd) {
                addItem(item);
            }
            return this;
        }

        public FishingPoolBuilder addAllItemStacks(List<ItemStack> itemStacks) {
            items.addAll(itemStacks);
            return this;
        }

        public List<ItemStack> build() {
            return items; // 返回不可变列表以确保线程安全
        }

}
