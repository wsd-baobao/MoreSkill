package com.zjw.moreskill;

import com.zjw.moreskill.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MoreSkillTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MoreSkill.MODID);

    public static final RegistryObject<CreativeModeTab> MORE_SKILL_TAB = CREATIVE_MODE_TABS
            .register("more_skill", () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT).icon(
                            () -> ModItems.ForgingHammerItem.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // output.accept(ModItems.ForgingHammerItem.get());
                        ModItems.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(output::accept);
                    }).build());
}
