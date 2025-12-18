package com.zjw.moreskill.screen;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.skill.alchemy.Alchemy;
import com.zjw.moreskill.skill.alchemy.AlchemyProvider;
import com.zjw.moreskill.skill.combat.Combat;
import com.zjw.moreskill.skill.combat.CombatProvider;
import com.zjw.moreskill.skill.cooking.Cooking;
import com.zjw.moreskill.skill.cooking.CookingProvider;
import com.zjw.moreskill.skill.farming.Farming;
import com.zjw.moreskill.skill.farming.FarmingProvider;
import com.zjw.moreskill.skill.fishing.Fishing;
import com.zjw.moreskill.skill.fishing.FishingSkillProvider;
import com.zjw.moreskill.skill.magic.Magic;
import com.zjw.moreskill.skill.mining.Mining;
import com.zjw.moreskill.skill.mining.MiningSkillProvider;
import com.zjw.moreskill.skill.smithing.Smithing;
import com.zjw.moreskill.skill.smithing.SmithingSkillProvider;
import com.zjw.moreskill.skill.trading.Trading;
import com.zjw.moreskill.skill.trading.TradingProvider;
import com.zjw.moreskill.skill.woodcutting.WoodCutting;
import com.zjw.moreskill.skill.woodcutting.WoodCuttingProvider;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class SkillPanelScreen extends Screen {
    private final Player player;

    // Skills
    private Fishing fishingSkill;
    private Mining miningSkill;
    private Smithing smithingSkill;
    private Combat combatSkill;
    private Cooking cookingSkill;
    private Farming farmingSkill;
    private Trading tradingSkill;
    private WoodCutting woodCuttingSkill;
    private Alchemy alchemySkill;
    private Magic magicSkill;

    private int hoveredSkillIndex = -1;

    public SkillPanelScreen(Player player) {
        super(Component.literal("Skill Panel"));
        this.player = player;
        // Initialize all skills
        initializeSkills();
    }

    private void initializeSkills() {
        player.getCapability(FishingSkillProvider.FISHING_SKILL).ifPresent(fishing -> fishingSkill = fishing);
        player.getCapability(MiningSkillProvider.MINING_SKILL).ifPresent(mining -> miningSkill = mining);
        player.getCapability(SmithingSkillProvider.SMITHING_SKILL).ifPresent(smithing -> smithingSkill = smithing);
        player.getCapability(CombatProvider.COMBAT_CAPABILITY).ifPresent(combat -> combatSkill = combat);
        player.getCapability(CookingProvider.COOKING_CAPABILITY).ifPresent(cooking -> cookingSkill = cooking);
        player.getCapability(FarmingProvider.FARMING_CAPABILITY).ifPresent(farming -> farmingSkill = farming);
        player.getCapability(TradingProvider.TRADING_CAPABILITY).ifPresent(trading -> tradingSkill = trading);
        player.getCapability(WoodCuttingProvider.WOODCUTTING_CAPABILITY)
                .ifPresent(woodCutting -> woodCuttingSkill = woodCutting);
        player.getCapability(AlchemyProvider.ALCHEMY_CAPABILITY).ifPresent(alchemy -> alchemySkill = alchemy);
        // player.getCapability(MagicProvider.MAGIC_CAPABILITY).ifPresent(magic ->
        // magicSkill = magic);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Fill background with a semi-transparent black
        renderBackground(graphics);

        // Calculate center of the screen
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Skill panel dimensions
        int panelWidth = 550;
        int panelHeight = 300;

        // Panel background
        graphics.fillGradient(
                centerX - panelWidth / 2,
                centerY - panelHeight / 2,
                centerX + panelWidth / 2,
                centerY + panelHeight / 2,
                0xC0000000,
                0x80000000);

        // Skill grid layout
        String[] skillNames = {
                fishingSkill != null ? fishingSkill.getName().getString() : "Fishing",
                miningSkill != null ? miningSkill.getName().getString() : "Mining",
                smithingSkill != null ? smithingSkill.getName().getString() : "Smithing",
                combatSkill != null ? combatSkill.getName().getString() : "Combat",
                cookingSkill != null ? cookingSkill.getName().getString() : "Cooking",
                farmingSkill != null ? farmingSkill.getName().getString() : "Farming",
                tradingSkill != null ? tradingSkill.getName().getString() : "Trading",
                woodCuttingSkill != null ? woodCuttingSkill.getName().getString() : "Woodcutting",
                alchemySkill != null ? alchemySkill.getName().getString() : "Alchemy",
                // magicSkill != null ? magicSkill.getName().getString() : "Magic"

        };

        // Skill levels and experiences
        int[] skillLevels = {
                fishingSkill != null ? fishingSkill.getLevel() : 0,
                miningSkill != null ? miningSkill.getLevel() : 0,
                smithingSkill != null ? smithingSkill.getLevel() : 0,
                combatSkill != null ? combatSkill.getLevel() : 0,
                cookingSkill != null ? cookingSkill.getLevel() : 0,
                farmingSkill != null ? farmingSkill.getLevel() : 0,
                tradingSkill != null ? tradingSkill.getLevel() : 0,
                woodCuttingSkill != null ? woodCuttingSkill.getLevel() : 0,
                alchemySkill != null ? alchemySkill.getLevel() : 0,
                // magicSkill != null ? magicSkill.getLevel() : 0
        };

        int[] skillExps = {
                fishingSkill != null ? fishingSkill.getExp() : 0,
                miningSkill != null ? miningSkill.getExp() : 0,
                smithingSkill != null ? smithingSkill.getExp() : 0,
                combatSkill != null ? combatSkill.getExp() : 0,
                cookingSkill != null ? cookingSkill.getExp() : 0,
                farmingSkill != null ? farmingSkill.getExp() : 0,
                tradingSkill != null ? tradingSkill.getExp() : 0,
                woodCuttingSkill != null ? woodCuttingSkill.getExp() : 0,
                alchemySkill != null ? alchemySkill.getExp() : 0,
                // magicSkill != null ? magicSkill.getExp() : 0
        };

        int[] skilllevelupexp = {
                fishingSkill != null ? fishingSkill.getExpForNextLevel() : 0,
                miningSkill != null ? miningSkill.getExpForNextLevel() : 0,
                smithingSkill != null ? smithingSkill.getExpForNextLevel() : 0,
                combatSkill != null ? combatSkill.getExpForNextLevel() : 0,
                cookingSkill != null ? cookingSkill.getExpForNextLevel() : 0,
                farmingSkill != null ? farmingSkill.getExpForNextLevel() : 0,

                tradingSkill != null ? tradingSkill.getExpForNextLevel() : 0,
                woodCuttingSkill != null ? woodCuttingSkill.getExpForNextLevel() : 0,
                alchemySkill != null ? alchemySkill.getExpForNextLevel() : 0,
                // magicSkill != null ? magicSkill.getExp() : 0
        };

        // Grid parameters
        int columns = 5;
        int rows = 2;
        int cellWidth = panelWidth / (columns + 1);
        int cellHeight = panelHeight / (rows + 1);

        hoveredSkillIndex = -1;

        // Render skill grid
        for (int i = 0; i < skillNames.length; i++) {
            int row = i / columns;
            int col = i % columns;

            int x = centerX - panelWidth / 2 + cellWidth * (col + 1) - 22;
            int y = centerY - panelHeight / 2 + cellHeight * (row + 1);
            
            
            //TODO: 通过经验来提升技能等级，如果拥有的经验足够升级，就显示为一个其他的颜色
            //TODO: 点击对应技能范围，消耗经验，提升技能等级。


            //TODO: 高亮范围不正确，没有居中。
            // 鼠标移动到技能位置添加高亮
            if (mouseX >= x && mouseX <= x + 100 && mouseY >= y && mouseY <= y + 30) {
                // Mouse is hovering over this skill
                hoveredSkillIndex = i;

                // Draw highlight background
                graphics.fill(x - 2, y - 2, x + 102, y + 32, 0x80FFFFFF);

                // Draw border
                graphics.fill(x - 2, y - 2, x + 102, y, 0xFFFFFFFF); // Top
                graphics.fill(x - 2, y + 30, x + 102, y + 32, 0xFFFFFFFF); // Bottom
                graphics.fill(x - 2, y - 2, x, y + 32, 0xFFFFFFFF); // Left
                graphics.fill(x + 100, y - 2, x + 102, y + 32, 0xFFFFFFFF); // Right
            }

            // Skill name and level 技能和经验的字体颜色
            graphics.drawString(
                    this.font,
                    skillNames[i] + " Lv: " + skillLevels[i],
                    x,
                    y,
                    hoveredSkillIndex == i ? 0xFFFF00 : 0xFFFFFF);

            graphics.drawString(
                    this.font,
                    "Exp: " + skillExps[i] + "/" + skilllevelupexp[i],
                    x,
                    y + 10,
                    hoveredSkillIndex == i ? 0xFFFFAA : 0xAAAAAA);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}