package com.zjw.moreskill.screen;

import com.zjw.moreskill.MoreSkill;
import com.zjw.moreskill.skill.fishing.Fishing;
import com.zjw.moreskill.skill.fishing.FishingSkillProvider;

import com.zjw.moreskill.skill.fishing.IFishing;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

public class SkillPanelScreen extends Screen {

    private static final ResourceLocation TEXTURE = new ResourceLocation(MoreSkill.MODID,
            "textures/gui/skill_panel.png");
    private final Player player;

    private Fishing fishingSkill;


    public SkillPanelScreen(Player player) {
        // 初始化代码
        super(Component.literal("Skill Panel"));
        this.player = player;


        player.getCapability(FishingSkillProvider.FISHING_SKILL).ifPresent(fishing -> {
            fishingSkill = (Fishing) fishing;
        });

    }

    @Override
    protected void init() {
        super.init();
        // 初始化按钮和其他组件
        refreshPlayerData();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        this.renderBackground(graphics);
        this.minecraft.getTextureManager().bindForSetup(TEXTURE);
        int x = (this.width - 256) / 2;
        int y = (this.height - 256) / 2;
        // blit(graphics, x, y, 0, 0, 256, 256, 256, 256);

        graphics.blit(TEXTURE, y, y, x, y, 256, 256);
        // 渲染技能数据
        // graphics.drawString(this.font, String.valueOf(Math.random()), x + 10, y + 5,
        // 0xFFFFFF);
        graphics.drawString(this.font, "Fishing Level: " + fishingSkill.getLevel(), x + 10, y + 10, 0xFFFFFF);
        graphics.drawString(
                this.font,
                "Fishing Exp: " + fishingSkill.getExp() + "/" + fishingSkill.getExpToNextLevel(), x + 10, y + 25,
                0xFFFFFF);


        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void refreshPlayerData() {
        fishingSkill = (Fishing) player.getCapability(FishingSkillProvider.FISHING_SKILL).resolve().get();
        System.out.println(fishingSkill.getExp());

        // 其他技能数据的刷新逻辑
    }


}