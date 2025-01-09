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

        //技能
        add("skill.moreskill.fishing", "Fishing");
        add("skill.moreskill.combat", "Combat");
        add("skill.moreskill.smithing", "Smithing");
        add("skill.moreskill.cooking", "Cooking");
        add("skill.moreskill.farming", "Farming");
        add("skill.moreskill.trading", "Trading");
        add("skill.moreskill.woodcutting", "Woodcutting");
        add("skill.moreskill.mining", "Mining");
        add("skill.moreskill.alchemy", "Alchemy");
        add("skill.moreskill.magic","Magic" );
    }
}
