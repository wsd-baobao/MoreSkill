package com.zjw.moreskill.screen;

import com.zjw.moreskill.attribute.AttributeData;
import com.zjw.moreskill.attribute.AttributeEffects;
import com.zjw.moreskill.attribute.AttributeProvider;
import com.zjw.moreskill.attribute.ModAttribute;
import com.zjw.moreskill.network.AllocateAttributePacket;
import com.zjw.moreskill.network.BuyAttributePointsPacket;
import com.zjw.moreskill.network.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class AttributePanelScreen extends Screen {
    private final Player player;
    private AttributeData attributeData;

    private static final int MARGIN = 40;
    private static final int TAB_HEIGHT = 24;
    private static final int HEADER_HEIGHT = 60;
    private static final int ROW_HEIGHT = 50;
    private static final int BTN_W = 60;
    private static final int BTN_H = 18;

    private int currentTab = 0;
    private int hoveredAttr = -1;
    private int panelX, panelY, panelW, panelH;

    public AttributePanelScreen(Player player) {
        super(Component.translatable("screen.moreskill.attributes"));
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();
        player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> attributeData = data);
        panelX = MARGIN;
        panelY = MARGIN;
        panelW = this.width - MARGIN * 2;
        panelH = this.height - MARGIN * 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        if (attributeData == null) {
            player.getCapability(AttributeProvider.ATTRIBUTE_CAPABILITY).ifPresent(data -> attributeData = data);
        }
        if (attributeData == null) {
            graphics.drawCenteredString(this.font,
                    Component.translatable("screen.moreskill.attributes.error"),
                    this.width / 2, this.height / 2, 0xFF5555);
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }

        graphics.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xC0101020);
        graphics.fill(panelX, panelY, panelX + panelW, panelY + 1, 0xFF444466);
        graphics.fill(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, 0xFF444466);
        graphics.fill(panelX, panelY, panelX + 1, panelY + panelH, 0xFF444466);
        graphics.fill(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, 0xFF444466);

        renderTabs(graphics, mouseX, mouseY);

        int contentY = panelY + TAB_HEIGHT + 4;

        if (currentTab == 0) {
            renderAttributesTab(graphics, mouseX, mouseY, contentY);
        } else {
            renderSummaryTab(graphics, contentY);
        }

        if (hoveredAttr >= 0 && currentTab == 0) {
            renderTooltip(graphics, mouseX, mouseY);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderTabs(GuiGraphics graphics, int mouseX, int mouseY) {
        String[] tabNames = {
                Component.translatable("screen.moreskill.attributes.tab.attributes").getString(),
                Component.translatable("screen.moreskill.attributes.tab.summary").getString()
        };
        int tabW = panelW / tabNames.length;

        for (int i = 0; i < tabNames.length; i++) {
            int tx = panelX + i * tabW;
            int ty = panelY;
            boolean active = i == currentTab;
            boolean hover = mouseX >= tx && mouseX < tx + tabW && mouseY >= ty && mouseY < ty + TAB_HEIGHT;

            graphics.fill(tx, ty, tx + tabW, ty + TAB_HEIGHT, active ? 0xFF202040 : (hover ? 0xFF181830 : 0xFF101020));
            if (active) {
                graphics.fill(tx, ty + TAB_HEIGHT - 2, tx + tabW, ty + TAB_HEIGHT, 0xFF6688CC);
            }
            graphics.drawCenteredString(this.font, tabNames[i], tx + tabW / 2, ty + 7,
                    active ? 0xFFFFFF : 0x888899);
        }
    }

    private void renderAttributesTab(GuiGraphics graphics, int mouseX, int mouseY, int contentY) {
        int totalXp = BuyAttributePointsPacket.getTotalExperience(player);
        int available = attributeData.getAvailablePoints();
        int nextCost = attributeData.getCostForNextPoint();

        graphics.drawString(this.font,
                Component.translatable("screen.moreskill.attributes.available_points", available),
                panelX + 12, contentY + 4, 0xFFCC44);
        graphics.drawString(this.font,
                Component.translatable("screen.moreskill.attributes.xp_total", totalXp),
                panelX + 12, contentY + 16, 0x55FF55);
        graphics.drawString(this.font,
                Component.translatable("screen.moreskill.attributes.next_cost", nextCost),
                panelX + 12, contentY + 28, 0xAAAAAA);

        int buyBtnX = panelX + panelW - BTN_W - 80;
        int buyBtnY = contentY + 8;
        boolean canBuy = totalXp >= nextCost;
        graphics.fill(buyBtnX, buyBtnY, buyBtnX + BTN_W, buyBtnY + BTN_H,
                canBuy ? 0xFF336633 : 0xFF333333);
        graphics.drawCenteredString(this.font,
                Component.translatable("screen.moreskill.attributes.buy_btn"),
                buyBtnX + BTN_W / 2, buyBtnY + 5, canBuy ? 0xFFFFFF : 0x666666);

        int buy10BtnX = buyBtnX + BTN_W + 6;
        int cost10 = attributeData.getXpForBuyAmount(10);
        boolean canBuy10 = totalXp >= cost10;
        graphics.fill(buy10BtnX, buyBtnY, buy10BtnX + BTN_W, buyBtnY + BTN_H,
                canBuy10 ? 0xFF336633 : 0xFF333333);
        graphics.drawCenteredString(this.font,
                Component.translatable("screen.moreskill.attributes.buy10_btn"),
                buy10BtnX + BTN_W / 2, buyBtnY + 5, canBuy10 ? 0xFFFFFF : 0x666666);

        int listY = contentY + HEADER_HEIGHT - 10;
        graphics.fill(panelX + 8, listY - 2, panelX + panelW - 8, listY - 1, 0xFF333355);

        ModAttribute[] attrs = ModAttribute.values();
        hoveredAttr = -1;

        for (int i = 0; i < attrs.length; i++) {
            ModAttribute attr = attrs[i];
            int rowY = listY + 6 + i * ROW_HEIGHT;
            int points = attributeData.getPoints(attr);
            boolean isMaxed = points >= attr.getMaxPoints();
            boolean hasPoints = available > 0 && !isMaxed;

            if (mouseX >= panelX + 10 && mouseX <= panelX + panelW - 10
                    && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT - 4) {
                hoveredAttr = i;
                graphics.fill(panelX + 10, rowY, panelX + panelW - 10, rowY + ROW_HEIGHT - 4, 0x30FFFFFF);
            }

            graphics.drawString(this.font, attr.getDisplayName(), panelX + 16, rowY + 4, 0xFFFFFF);

            String levelStr = points + " / " + attr.getMaxPoints();
            graphics.drawString(this.font, levelStr, panelX + 16, rowY + 18, 0xAAAAAA);

            int barX = panelX + 100;
            int barY = rowY + 20;
            int barW = panelW - 260;
            int barH = 6;
            graphics.fill(barX, barY, barX + barW, barY + barH, 0xFF222233);
            int filledW = (int) ((float) points / attr.getMaxPoints() * barW);
            int barColor = switch (attr) {
                case STRENGTH -> 0xFFCC4444;
                case AGILITY -> 0xFF44CC44;
                case INTELLIGENCE -> 0xFF4488FF;
                case VITALITY -> 0xFFCCAA44;
                case LUCK -> 0xFFCC44CC;
            };
            graphics.fill(barX, barY, barX + filledW, barY + barH, barColor);

            int allocBtnX = panelX + panelW - 50;
            int allocBtnY = rowY + 12;
            if (hasPoints) {
                graphics.fill(allocBtnX, allocBtnY, allocBtnX + 30, allocBtnY + 20, 0xFF336633);
                graphics.drawCenteredString(this.font, "+1", allocBtnX + 15, allocBtnY + 6, 0xFFFFFF);
            } else {
                graphics.fill(allocBtnX, allocBtnY, allocBtnX + 30, allocBtnY + 20, 0xFF333333);
                graphics.drawCenteredString(this.font, "+1", allocBtnX + 15, allocBtnY + 6, 0x555555);
            }

            if (isMaxed) {
                graphics.drawString(this.font,
                        Component.translatable("screen.moreskill.attributes.maxed").getString(),
                        allocBtnX - 36, rowY + 16, 0xFFAA00);
            }
        }
    }

    private void renderSummaryTab(GuiGraphics graphics, int contentY) {
        int y = contentY + 8;
        graphics.drawString(this.font,
                Component.translatable("screen.moreskill.attributes.summary.title"),
                panelX + 16, y, 0xFFCC44);
        y += 16;

        ModAttribute[] attrs = ModAttribute.values();
        for (ModAttribute attr : attrs) {
            int pts = attributeData.getPoints(attr);
            if (pts == 0) continue;

            graphics.drawString(this.font, attr.getDisplayName(), panelX + 16, y, 0xFFFFFF);
            y += 12;

            List<String> effects = getEffectDetails(attr, pts);
            for (String effect : effects) {
                graphics.drawString(this.font, "  " + effect, panelX + 24, y, 0x88CCFF);
                y += 10;
            }
            y += 6;
        }

        if (attributeData.getTotalAllocated() == 0) {
            graphics.drawCenteredString(this.font,
                    Component.translatable("screen.moreskill.attributes.summary.empty"),
                    panelX + panelW / 2, panelY + panelH / 2, 0x888899);
        }
    }

    private void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (hoveredAttr < 0) return;
        ModAttribute attr = ModAttribute.values()[hoveredAttr];
        int pts = attributeData.getPoints(attr);

        List<String> lines = new ArrayList<>();
        lines.add(attr.getDisplayName().getString() + " [" + pts + "/" + attr.getMaxPoints() + "]");
        lines.addAll(getEffectDetails(attr, pts));
        if (pts < attr.getMaxPoints()) {
            lines.add("");
            lines.addAll(getEffectDetails(attr, pts + 1));
        }

        int tooltipW = 0;
        for (String line : lines) {
            tooltipW = Math.max(tooltipW, this.font.width(line));
        }
        tooltipW += 8;

        int tx = mouseX + 12;
        int ty = mouseY - 4;
        int th = lines.size() * 10 + 6;

        if (tx + tooltipW > this.width - 4) tx = mouseX - tooltipW - 4;
        if (ty + th > this.height - 4) ty = this.height - th - 4;

        graphics.fill(tx, ty, tx + tooltipW, ty + th, 0xF0101030);
        graphics.fill(tx, ty, tx + tooltipW, ty + 1, 0xFF6666AA);
        graphics.fill(tx, ty + th - 1, tx + tooltipW, ty + th, 0xFF6666AA);
        graphics.fill(tx, ty, tx + 1, ty + th, 0xFF6666AA);
        graphics.fill(tx + tooltipW - 1, ty, tx + tooltipW, ty + th, 0xFF6666AA);

        for (int i = 0; i < lines.size(); i++) {
            int color = i == 0 ? 0xFFFFFF : (lines.get(i).isEmpty() ? 0 : 0x88CCFF);
            graphics.drawString(this.font, lines.get(i), tx + 4, ty + 3 + i * 10, color);
        }
    }

    private List<String> getEffectDetails(ModAttribute attr, int pts) {
        List<String> lines = new ArrayList<>();
        switch (attr) {
            case STRENGTH -> {
                lines.add(Component.translatable("screen.moreskill.attributes.effect.atk_dmg",
                        String.format("+%.1f", AttributeEffects.getAttackDamageBonus(pts))).getString());
                lines.add(Component.translatable("screen.moreskill.attributes.effect.crit_dmg",
                        String.format("+%.0f%%", AttributeEffects.getCriticalDamageBonus(pts) * 100)).getString());
                lines.add(Component.translatable("screen.moreskill.attributes.effect.armor",
                        String.format("+%.1f", AttributeEffects.getArmorBonus(pts))).getString());
            }
            case AGILITY -> {
                lines.add(Component.translatable("screen.moreskill.attributes.effect.speed",
                        String.format("+%.1f%%", AttributeEffects.getSpeedBonus(pts) * 100)).getString());
                lines.add(Component.translatable("screen.moreskill.attributes.effect.crit_rate",
                        String.format("+%.1f%%", AttributeEffects.getCritRateBonus(pts) * 100)).getString());
                lines.add(Component.translatable("screen.moreskill.attributes.effect.atk_speed",
                        String.format("+%.1f%%", AttributeEffects.getAttackSpeedBonus(pts) * 100)).getString());
                lines.add(Component.translatable("screen.moreskill.attributes.effect.dodge",
                        String.format("+%.1f%%", AttributeEffects.getDodgeChance(pts) * 100)).getString());
            }
            case INTELLIGENCE -> {
                lines.add(Component.translatable("screen.moreskill.attributes.effect.potion_dur",
                        String.format("+%.0f%%", (AttributeEffects.getPotionDurationMultiplier(pts) - 1) * 100)).getString());
            }
            case VITALITY -> {
                lines.add(Component.translatable("screen.moreskill.attributes.effect.toughness",
                        String.format("+%.1f", AttributeEffects.getArmorToughnessBonus(pts))).getString());
                lines.add(Component.translatable("screen.moreskill.attributes.effect.regen",
                        String.format("+%.1f%%", AttributeEffects.getHealthRegenBonus(pts) * 100)).getString());
                lines.add(Component.translatable("screen.moreskill.attributes.effect.kb_resist",
                        String.format("+%.1f%%", AttributeEffects.getKnockbackResistanceBonus(pts) * 100)).getString());
            }
            case LUCK -> {
                lines.add(Component.translatable("screen.moreskill.attributes.effect.xp_gain",
                        String.format("+%.1f%%", AttributeEffects.getXpGainBonus(pts) * 100)).getString());
                lines.add(Component.translatable("screen.moreskill.attributes.effect.mob_drop",
                        String.format("+%.1f%%", AttributeEffects.getMobDropBonus(pts) * 100)).getString());
                lines.add(Component.translatable("screen.moreskill.attributes.effect.mining_drop",
                        String.format("+%.1f%%", AttributeEffects.getMiningDropBonus(pts) * 100)).getString());
                lines.add(Component.translatable("screen.moreskill.attributes.effect.fishing",
                        String.format("+%.1f%%", AttributeEffects.getFishingBonus(pts) * 100)).getString());
                lines.add(Component.translatable("screen.moreskill.attributes.effect.luck_crit",
                        String.format("+%.1f%%", AttributeEffects.getLuckCritBonus(pts) * 100)).getString());
                lines.add(Component.translatable("screen.moreskill.attributes.effect.luck_dodge",
                        String.format("+%.1f%%", AttributeEffects.getLuckDodgeBonus(pts) * 100)).getString());
            }
        }
        return lines;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (attributeData == null) return super.mouseClicked(mouseX, mouseY, button);

        int tabW = panelW / 2;
        if (mouseY >= panelY && mouseY < panelY + TAB_HEIGHT) {
            if (mouseX >= panelX && mouseX < panelX + tabW) {
                currentTab = 0;
                return true;
            } else if (mouseX >= panelX + tabW && mouseX < panelX + panelW) {
                currentTab = 1;
                return true;
            }
        }

        if (currentTab == 0) {
            int contentY = panelY + TAB_HEIGHT + 4;
            int buyBtnX = panelX + panelW - BTN_W - 80;
            int buyBtnY = contentY + 8;

            if (mouseX >= buyBtnX && mouseX <= buyBtnX + BTN_W
                    && mouseY >= buyBtnY && mouseY <= buyBtnY + BTN_H) {
                int totalXp = BuyAttributePointsPacket.getTotalExperience(player);
                int cost = attributeData.getCostForNextPoint();
                if (totalXp >= cost) {
                    NetworkHandler.INSTANCE.sendToServer(new BuyAttributePointsPacket(totalXp));
                }
                return true;
            }

            int buy10BtnX = buyBtnX + BTN_W + 6;
            if (mouseX >= buy10BtnX && mouseX <= buy10BtnX + BTN_W
                    && mouseY >= buyBtnY && mouseY <= buyBtnY + BTN_H) {
                int totalXp = BuyAttributePointsPacket.getTotalExperience(player);
                int cost10 = attributeData.getXpForBuyAmount(10);
                if (totalXp >= cost10) {
                    NetworkHandler.INSTANCE.sendToServer(new BuyAttributePointsPacket(cost10));
                }
                return true;
            }

            int listY = contentY + HEADER_HEIGHT - 10;
            ModAttribute[] attrs = ModAttribute.values();
            for (int i = 0; i < attrs.length; i++) {
                int rowY = listY + 6 + i * ROW_HEIGHT;
                int allocBtnX = panelX + panelW - 50;
                int allocBtnY = rowY + 12;

                if (mouseX >= allocBtnX && mouseX <= allocBtnX + 30
                        && mouseY >= allocBtnY && mouseY <= allocBtnY + 20) {
                    if (attributeData.getAvailablePoints() > 0
                            && attributeData.getPoints(attrs[i]) < attrs[i].getMaxPoints()) {
                        NetworkHandler.INSTANCE.sendToServer(new AllocateAttributePacket(attrs[i].getId()));
                    }
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
