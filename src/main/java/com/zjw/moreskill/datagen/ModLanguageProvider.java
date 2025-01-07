package com.zjw.moreskill.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.more_skill", "More Skill");
        add("item.moreskill.forging_hammer_item", "Forging Hammer");
        add("key.categories.moreskill", "More Skill");
        add("key.moreskill.open_ui", "Open UI");
    }
}
