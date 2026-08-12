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

    private static final int MARGIN = 60;
    private static final int TAB_BAR_H = 22;
    private static final int TAB_W = 56;
    private static final int TAB_H = 18;
    private static final int INFO_H = 28;
    private static final int ROW_H = 24;
    private static final int BUY_BTN_W = 46;
    private static final int BUY_BTN_H = 16;
    private static final int ALLOC_BTN_W = 26;
    private static final int ALLOC_BTN_H = 16;

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

        int contentY = panelY + TAB_BAR_H;

        if (currentTab == 0) {
            renderAttributesTab(graphics, mouseX, mouseY, contentY);
        } else {
            graphics.enableScissor(panelX, contentY, panelX + panelW, panelY + panelH);
            renderSummaryTab(graphics, contentY);
            graphics.disableScissor();
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

        int tx = panelX + 4;
        int ty = panelY + 2;

        for (int i = 0; i < tabNames.length; i++) {
            int bx = tx + i * (TAB_W + 3);
            boolean active = i == currentTab;
            boolean hover = mouseX >= bx && mouseX < bx + TAB_W && mouseY >= ty && mouseY < ty + TAB_H;

            graphics.fill(bx, ty, bx + TAB_W, ty + TAB_H, active ? 0xFF282850 : (hover ? 0xFF1C1C38 : 0xFF141428));
            if (active) {
                graphics.fill(bx, ty + TAB_H - 2, bx + TAB_W, ty + TAB_H, 0xFF6688CC);
            }
            graphics.fill(bx, ty, bx + TAB_W, ty + 1, 0xFF444466);
            graphics.fill(bx, ty, bx + 1, ty + TAB_H, 0xFF444466);
            graphics.fill(bx + TAB_W - 1, ty, bx + TAB_W, ty + TAB_H, 0xFF444466);
            graphics.fill(bx, ty + TAB_H - 1, bx + TAB_W, ty + TAB_H, 0xFF444466);

            graphics.drawCenteredString(this.font, tabNames[i], bx + TAB_W / 2, ty + 5,
                    active ? 0xFFFFFF : 0x888899);
        }
    }

    private void renderAttributesTab(GuiGraphics graphics, int mouseX, int mouseY, int contentY) {
        int totalXp = BuyAttributePointsPacket.getTotalExperience(player);
        int available = attributeData.getAvailablePoints();
        int nextCost = attributeData.getCostForNextPoint();

        int infoY = contentY + 2;
        graphics.drawString(this.font,
                Component.translatable("screen.moreskill.attributes.available_points", available),
                panelX + 10, infoY + 2, 0xFFCC44);

        int xpX = panelX + 10 + this.font.width(
                Component.translatable("screen.moreskill.attributes.available_points", available).getString()) + 10;
        graphics.drawString(this.font,
                Component.translatable("screen.moreskill.attributes.xp_total", totalXp),
                xpX, infoY + 2, 0x55FF55);

        int buyBtnX = panelX + panelW - BUY_BTN_W * 2 - 14;
        int buyBtnY = infoY;
        boolean canBuy = totalXp >= nextCost;
        graphics.fill(buyBtnX, buyBtnY, buyBtnX + BUY_BTN_W, buyBtnY + BUY_BTN_H,
                canBuy ? 0xFF336633 : 0xFF333333);
        graphics.drawCenteredString(this.font,
                Component.translatable("screen.moreskill.attributes.buy_btn"),
                buyBtnX + BUY_BTN_W / 2, buyBtnY + 4, canBuy ? 0xFFFFFF : 0x666666);

        int buy10BtnX = buyBtnX + BUY_BTN_W + 4;
        int cost10 = attributeData.getXpForBuyAmount(10);
        boolean canBuy10 = totalXp >= cost10;
        graphics.fill(buy10BtnX, buyBtnY, buy10BtnX + BUY_BTN_W, buyBtnY + BUY_BTN_H,
                canBuy10 ? 0xFF336633 : 0xFF333333);
        graphics.drawCenteredString(this.font,
                Component.translatable("screen.moreskill.attributes.buy10_btn"),
                buy10BtnX + BUY_BTN_W / 2, buyBtnY + 4, canBuy10 ? 0xFFFFFF : 0x666666);

        int costX = buyBtnX - 8;
        String costStr = Component.translatable("screen.moreskill.attributes.next_cost", nextCost).getString();
        int costW = this.font.width(costStr);
        graphics.drawString(this.font, costStr,
                costX - costW, infoY + 2, 0xAAAAAA);

        int sepY = contentY + INFO_H;
        graphics.fill(panelX + 4, sepY, panelX + panelW - 4, sepY + 1, 0xFF333355);

        int listY = sepY + 3;

        ModAttribute[] attrs = ModAttribute.values();
        hoveredAttr = -1;

        graphics.enableScissor(panelX, listY, panelX + panelW, panelY + panelH - 2);
        for (int i = 0; i < attrs.length; i++) {
            ModAttribute attr = attrs[i];
            int rowY = listY + i * ROW_H;

            if (rowY + ROW_H - 2 > panelY + panelH - 2) break;

            int points = attributeData.getPoints(attr);
            boolean hasPoints = available > 0;

            if (mouseX >= panelX + 6 && mouseX <= panelX + panelW - 6
                    && mouseY >= rowY && mouseY <= rowY + ROW_H - 2) {
                hoveredAttr = i;
                graphics.fill(panelX + 6, rowY, panelX + panelW - 6, rowY + ROW_H - 2, 0x30FFFFFF);
            }

            graphics.drawString(this.font, attr.getDisplayName(), panelX + 12, rowY + 4, 0xFFFFFF);

            String levelStr = "[" + points + "]";
            int nameW = this.font.width(attr.getDisplayName().getString());
            graphics.drawString(this.font, levelStr, panelX + 12 + nameW + 6, rowY + 4, 0xAAAAAA);

            int allocBtnX = panelX + panelW - ALLOC_BTN_W - 8;
            int allocBtnY = rowY + (ROW_H - ALLOC_BTN_H) / 2 - 1;
            if (hasPoints) {
                graphics.fill(allocBtnX, allocBtnY, allocBtnX + ALLOC_BTN_W, allocBtnY + ALLOC_BTN_H, 0xFF336633);
                graphics.drawCenteredString(this.font, "+1", allocBtnX + ALLOC_BTN_W / 2, allocBtnY + 4, 0xFFFFFF);
            } else {
                graphics.fill(allocBtnX, allocBtnY, allocBtnX + ALLOC_BTN_W, allocBtnY + ALLOC_BTN_H, 0xFF333333);
                graphics.drawCenteredString(this.font, "+1", allocBtnX + ALLOC_BTN_W / 2, allocBtnY + 4, 0x555555);
            }
        }
        graphics.disableScissor();
    }

    private void renderSummaryTab(GuiGraphics graphics, int contentY) {
        int y = contentY + 6;
        graphics.drawString(this.font,
                Component.translatable("screen.moreskill.attributes.summary.title"),
                panelX + 12, y, 0xFFCC44);
        y += 14;

        ModAttribute[] attrs = ModAttribute.values();
        for (ModAttribute attr : attrs) {
            int pts = attributeData.getPoints(attr);
            if (pts == 0) continue;

            graphics.drawString(this.font, attr.getDisplayName(), panelX + 12, y, 0xFFFFFF);
            y += 11;

            List<String> effects = getEffectDetails(attr, pts);
            for (String effect : effects) {
                graphics.drawString(this.font, "  " + effect, panelX + 20, y, 0x88CCFF);
                y += 10;
            }
            y += 4;
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
        lines.add(attr.getDisplayName().getString() + " [" + pts + "]");
        lines.addAll(getEffectDetails(attr, pts));

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

        int tx = panelX + 4;
        int ty = panelY + 2;
        for (int i = 0; i < 2; i++) {
            int bx = tx + i * (TAB_W + 3);
            if (mouseX >= bx && mouseX < bx + TAB_W && mouseY >= ty && mouseY < ty + TAB_H) {
                currentTab = i;
                return true;
            }
        }

        if (currentTab == 0) {
            int contentY = panelY + TAB_BAR_H;
            int buyBtnX = panelX + panelW - BUY_BTN_W * 2 - 14;
            int buyBtnY = contentY + 2;

            if (mouseX >= buyBtnX && mouseX <= buyBtnX + BUY_BTN_W
                    && mouseY >= buyBtnY && mouseY <= buyBtnY + BUY_BTN_H) {
                int totalXp = BuyAttributePointsPacket.getTotalExperience(player);
                int cost = attributeData.getCostForNextPoint();
                if (totalXp >= cost) {
                    NetworkHandler.INSTANCE.sendToServer(new BuyAttributePointsPacket(cost));
                }
                return true;
            }

            int buy10BtnX = buyBtnX + BUY_BTN_W + 4;
            if (mouseX >= buy10BtnX && mouseX <= buy10BtnX + BUY_BTN_W
                    && mouseY >= buyBtnY && mouseY <= buyBtnY + BUY_BTN_H) {
                int totalXp = BuyAttributePointsPacket.getTotalExperience(player);
                int cost10 = attributeData.getXpForBuyAmount(10);
                if (totalXp >= cost10) {
                    NetworkHandler.INSTANCE.sendToServer(new BuyAttributePointsPacket(cost10));
                }
                return true;
            }

            int sepY = contentY + INFO_H;
            int listY = sepY + 3;
            ModAttribute[] attrs = ModAttribute.values();
            for (int i = 0; i < attrs.length; i++) {
                int rowY = listY + i * ROW_H;
                int allocBtnX = panelX + panelW - ALLOC_BTN_W - 8;
                int allocBtnY = rowY + (ROW_H - ALLOC_BTN_H) / 2 - 1;

                if (mouseX >= allocBtnX && mouseX <= allocBtnX + ALLOC_BTN_W
                        && mouseY >= allocBtnY && mouseY <= allocBtnY + ALLOC_BTN_H) {
                    if (attributeData.getAvailablePoints() > 0) {
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
