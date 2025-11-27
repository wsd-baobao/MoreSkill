package com.zjw.moreskill.item;

import com.zjw.moreskill.MoreSkill;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MoreSkill.MODID);

    public static final RegistryObject<Item> ForgingHammerItem = ITEMS.register("forging_hammer_item",
         ForgingHammerItem::new);
    

}
