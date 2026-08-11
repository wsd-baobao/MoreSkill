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


        add("moreskill.alchemy.duration_increase", "Potion Duration: +%d minutes");
        //锻造
        add("message.moreskill.smithing_exp_gain", "You gained %d Smithing experience!");
        add("message.moreskill.absorption_conversion", "Converted %d absorption to absorption hearts!");
        add("message.moreskill.absorption_gained", "Gained %.1f absorption!");
        add("tooltip.smithing.author", "Author: %s");
        add("tooltip.smithing.attack_damage", "Attack Damage: +%d");
        add("tooltip.smithing.attack_speed", "Attack Speed: +%d");
        add("tooltip.smithing.durability", "Durability: +%d");
        add("tooltip.smithing.armor", "Armor: +%d");
        add("tooltip.smithing.armor_toughness", "Armor Toughness: +%d");
        add("tooltip.smithing.break_speed", "Break Speed: +%d");
        add("tooltip.smithing.knockback_resistance", "Knockback Resistance: +%d");
        add("tooltip.smithing.critical_strike_chance", "Critical Strike Chance: +%f%%");
        add("tooltip.smithing.max_health", "Max Health: +%d");
        add("tooltip.smithing.move_speed", "Move Speed: +%f%%");
        add("tooltip.smithing.absorption", "Absorption: +%f");
        add("tooltip.smithing.thorns", "Thorns: +%f%%");

        //钓鱼
        add("message.moreskill.fishing_exp_gain", "You gained %d fishing experience!");

        //种植
        add("message.farming.plant_exp", "You gained %d plant experience!");
        add("message.farming.bonemeal_saved", "Saved bonemeal!");
        add("message.farming.bonemeal_exp", "You gained %d bonemeal experience!");
        add("message.farming.crop_growth", "Boosted crop growth!");
        add("message.farming.harvest_exp", "You gained %d harvest experience!");
        add("message.farming.harvest_items","Harvested Items: %s");

        // Attribute System
        add("key.moreskill.open_attribute_ui", "Open Attribute Panel");
        add("screen.moreskill.attributes", "Attributes");
        add("screen.moreskill.attributes.title", "Attribute Points");
        add("screen.moreskill.attributes.xp_level", "Available XP Levels: %d");
        add("screen.moreskill.attributes.level", "%d/%d");
        add("screen.moreskill.attributes.cost", "Cost: %d");
        add("screen.moreskill.attributes.maxed", "MAX");
        add("screen.moreskill.attributes.error", "Failed to load attribute data");
        add("screen.moreskill.attributes.effect.strength", "ATK %s");
        add("screen.moreskill.attributes.effect.agility", "SPD %s");
        add("screen.moreskill.attributes.effect.intelligence", "POT %s");
        add("screen.moreskill.attributes.effect.vitality", "HP %s");
        add("screen.moreskill.attributes.effect.luck", "LUK %s");

        add("attribute.moreskill.strength", "Strength");
        add("attribute.moreskill.strength.desc", "Increases attack damage");
        add("attribute.moreskill.agility", "Agility");
        add("attribute.moreskill.agility.desc", "Increases movement speed");
        add("attribute.moreskill.intelligence", "Intelligence");
        add("attribute.moreskill.intelligence.desc", "Increases potion duration");
        add("attribute.moreskill.vitality", "Vitality");
        add("attribute.moreskill.vitality.desc", "Increases maximum health");
        add("attribute.moreskill.luck", "Luck");
        add("attribute.moreskill.luck.desc", "Increases luck for better drops");

        add("message.moreskill.attribute.allocated", "Allocated %s to level %d");
        add("message.moreskill.attribute.maxed", "%s is already at max level!");
        add("message.moreskill.attribute.not_enough_xp", "Not enough XP levels! Need %d, have %d");

    }
}
