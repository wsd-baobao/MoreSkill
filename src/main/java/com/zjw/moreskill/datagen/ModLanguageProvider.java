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
        add("screen.moreskill.attributes.tab.attributes", "Attributes");
        add("screen.moreskill.attributes.tab.summary", "Summary");
        add("screen.moreskill.attributes.available_points", "Available Points: %d");
        add("screen.moreskill.attributes.xp_total", "Experience: %d");
        add("screen.moreskill.attributes.next_cost", "Next Point: %d XP");
        add("screen.moreskill.attributes.buy_btn", "Buy");
        add("screen.moreskill.attributes.buy10_btn", "Buy 10");
        add("screen.moreskill.attributes.maxed", "MAX");
        add("screen.moreskill.attributes.error", "Failed to load attribute data");
        add("screen.moreskill.attributes.summary.title", "Attribute Bonus Summary");
        add("screen.moreskill.attributes.summary.empty", "No attribute points allocated yet");
        add("screen.moreskill.attributes.effect.atk_dmg", "Attack Damage %s");
        add("screen.moreskill.attributes.effect.crit_dmg", "Crit Damage %s");
        add("screen.moreskill.attributes.effect.armor", "Armor %s");
        add("screen.moreskill.attributes.effect.speed", "Move Speed %s");
        add("screen.moreskill.attributes.effect.crit_rate", "Crit Rate %s");
        add("screen.moreskill.attributes.effect.atk_speed", "Attack Speed %s");
        add("screen.moreskill.attributes.effect.dodge", "Dodge Chance %s");
        add("screen.moreskill.attributes.effect.potion_dur", "Potion Duration %s");
        add("screen.moreskill.attributes.effect.toughness", "Armor Toughness %s");
        add("screen.moreskill.attributes.effect.regen", "Health Regen +%s/s");
        add("screen.moreskill.attributes.effect.kb_resist", "Knockback Resist %s");
        add("screen.moreskill.attributes.effect.xp_gain", "XP Gain %s");
        add("screen.moreskill.attributes.effect.mob_drop", "Mob Drops %s");
        add("screen.moreskill.attributes.effect.mining_drop", "Mining Drops %s");
        add("screen.moreskill.attributes.effect.fishing", "Fishing %s");
        add("screen.moreskill.attributes.effect.luck_crit", "Luck Crit %s");
        add("screen.moreskill.attributes.effect.luck_dodge", "Luck Dodge %s");

        add("attribute.moreskill.strength", "Strength");
        add("attribute.moreskill.strength.desc", "Attack damage, crit damage, armor");
        add("attribute.moreskill.agility", "Agility");
        add("attribute.moreskill.agility.desc", "Move speed, crit rate, attack speed, dodge");
        add("attribute.moreskill.intelligence", "Intelligence");
        add("attribute.moreskill.intelligence.desc", "Potion duration");
        add("attribute.moreskill.vitality", "Vitality");
        add("attribute.moreskill.vitality.desc", "Armor toughness, health regen, knockback resist");
        add("attribute.moreskill.luck", "Luck");
        add("attribute.moreskill.luck.desc", "XP gain, drops, fishing, crit, dodge");

        add("message.moreskill.attribute.allocated", "Allocated %s to %d points");
        add("message.moreskill.attribute.maxed", "%s is already at max level!");
        add("message.moreskill.attribute.no_points_available", "No attribute points available! Buy some with XP first");
        add("message.moreskill.attribute.not_enough_xp_points", "Not enough experience!");
        add("message.moreskill.attribute.need_more_xp", "Not enough XP! Need %d XP");
        add("message.moreskill.attribute.points_bought", "Bought %d attribute points for %d XP");

    }
}
