package com.zjw.moreskill.screen;

import com.zjw.moreskill.attribute.AttributeData;
import com.zjw.moreskill.attribute.AttributeEffects;
import com.zjw.moreskill.attribute.AttributeProvider;
import com.zjw.moreskill.attribute.ModAttribute;
import com.zjw.moreskill.network.AllocateAttributePacket;
import com.zjw.moreskill.network.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class AttributePanelScreen extends Screen {
    private final Player player;
    private AttributeData attributeData;

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 280;
    private static final int ROW_HEIGHT = 40;
    private static final int BUTTON_SIZE = 16;

    private int hoveredRow = -1;

    public AttributePanelScreen(Player player) {
        super(Component.translatable("screen.moreskill.attributes"));
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();
        player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> attributeData = data);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelX = centerX - PANEL_WIDTH / 2;
        int panelY = centerY - PANEL_HEIGHT / 2;

        graphics.fillGradient(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT,
                0xC0000000, 0x80000000);

        graphics.drawCenteredString(this.font,
                Component.translatable("screen.moreskill.attributes.title"),
                centerX, panelY + 10, 0xFFFFFF);

        int xpLevel = player.experienceLevel;
        graphics.drawCenteredString(this.font,
                Component.translatable("screen.moreskill.attributes.xp_level", xpLevel),
                centerX, panelY + 24, 0x55FF55);

        if (attributeData == null) {
            player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> attributeData = data);
        }

        if (attributeData == null) {
            graphics.drawCenteredString(this.font,
                    Component.translatable("screen.moreskill.attributes.error"),
                    centerX, centerY, 0xFF5555);
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }

        ModAttribute[] attributes = ModAttribute.values();
        int listStartY = panelY + 42;
        hoveredRow = -1;

        for (int i = 0; i < attributes.length; i++) {
            ModAttribute attr = attributes[i];
            int rowY = listStartY + i * ROW_HEIGHT;
            int points = attributeData.getPoints(attr);
            int cost = attributeData.getCostForNextPoint(attr);
            boolean isMaxed = points >= attr.getMaxPoints();
            boolean canAfford = xpLevel >= cost && !isMaxed;

            if (mouseX >= panelX + 10 && mouseX <= panelX + PANEL_WIDTH - 10
                    && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT - 4) {
                hoveredRow = i;
                graphics.fill(panelX + 10, rowY, panelX + PANEL_WIDTH - 10, rowY + ROW_HEIGHT - 4,
                        0x40FFFFFF);
            }

            graphics.drawString(this.font, attr.getDisplayName(), panelX + 16, rowY + 4, 0xFFFFFF);

            String levelText = Component.translatable("screen.moreskill.attributes.level",
                    points, attr.getMaxPoints()).getString();
            graphics.drawString(this.font, levelText, panelX + 16, rowY + 16, 0xAAAAAA);

            String effectText = getEffectPreview(attr, points);
            graphics.drawString(this.font, effectText, panelX + 120, rowY + 4, 0x88CCFF);

            int btnX = panelX + PANEL_WIDTH - 50;
            int btnY = rowY + 6;

            if (canAfford) {
                graphics.fill(btnX, btnY, btnX + BUTTON_SIZE, btnY + BUTTON_SIZE, 0xFF44AA44);
                graphics.drawCenteredString(this.font, "+", btnX + BUTTON_SIZE / 2, btnY + 4, 0xFFFFFF);
            } else {
                graphics.fill(btnX, btnY, btnX + BUTTON_SIZE, btnY + BUTTON_SIZE, 0xFF555555);
                graphics.drawCenteredString(this.font, "+", btnX + BUTTON_SIZE / 2, btnY + 4, 0x888888);
            }

            String costText;
            if (isMaxed) {
                costText = Component.translatable("screen.moreskill.attributes.maxed").getString();
                graphics.drawString(this.font, costText, panelX + PANEL_WIDTH - 28, rowY + 4, 0xFFAA00);
            } else {
                costText = Component.translatable("screen.moreskill.attributes.cost", cost).getString();
                graphics.drawString(this.font, costText, panelX + PANEL_WIDTH - 28, rowY + 4,
                        canAfford ? 0x55FF55 : 0xFF5555);
            }

            if (hoveredRow == i && mouseX >= btnX && mouseX <= btnX + BUTTON_SIZE
                    && mouseY >= btnY && mouseY <= btnY + BUTTON_SIZE && canAfford) {
                graphics.fill(btnX - 1, btnY - 1, btnX + BUTTON_SIZE + 1, btnY + BUTTON_SIZE + 1,
                        0xFFFFFFFF);
            }
        }

        if (hoveredRow >= 0) {
            ModAttribute hoveredAttr = attributes[hoveredRow];
            int descY = panelY + PANEL_HEIGHT - 20;
            graphics.drawCenteredString(this.font, hoveredAttr.getDescription(),
                    centerX, descY, 0xCCCCCC);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (attributeData == null) return super.mouseClicked(mouseX, mouseY, button);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelX = centerX - PANEL_WIDTH / 2;
        int panelY = centerY - PANEL_HEIGHT / 2;
        int listStartY = panelY + 42;

        ModAttribute[] attributes = ModAttribute.values();

        for (int i = 0; i < attributes.length; i++) {
            int rowY = listStartY + i * ROW_HEIGHT;
            int btnX = panelX + PANEL_WIDTH - 50;
            int btnY = rowY + 6;

            if (mouseX >= btnX && mouseX <= btnX + BUTTON_SIZE
                    && mouseY >= btnY && mouseY <= btnY + BUTTON_SIZE) {
                ModAttribute attr = attributes[i];
                int points = attributeData.getPoints(attr);
                int cost = attributeData.getCostForNextPoint(attr);

                if (points < attr.getMaxPoints() && player.experienceLevel >= cost) {
                    NetworkHandler.INSTANCE.sendToServer(new AllocateAttributePacket(attr.getId()));
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private String getEffectPreview(ModAttribute attr, int points) {
        return switch (attr) {
            case STRENGTH -> Component.translatable("screen.moreskill.attributes.effect.strength",
                    String.format("+%.1f", AttributeEffects.getAttackDamageBonus(points))).getString();
            case AGILITY -> Component.translatable("screen.moreskill.attributes.effect.agility",
                    String.format("+%.1f%%", AttributeEffects.getSpeedBonus(points) * 100)).getString();
            case INTELLIGENCE -> Component.translatable("screen.moreskill.attributes.effect.intelligence",
                    String.format("+%.0f%%", (AttributeEffects.getPotionDurationMultiplier(points) - 1) * 100)).getString();
            case VITALITY -> Component.translatable("screen.moreskill.attributes.effect.vitality",
                    String.format("+%.1f", AttributeEffects.getMaxHealthBonus(points))).getString();
            case LUCK -> Component.translatable("screen.moreskill.attributes.effect.luck",
                    String.format("+%.1f", AttributeEffects.getLuckBonus(points))).getString();
        };
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
