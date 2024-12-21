package com.zjw.moreskill.menu;

import com.zjw.moreskill.MoreSkill;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {

    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MoreSkill.MODID);

    // public static final RegistryObject<MenuType<SkillPanelMenu>> SKILL_PANEL = register("skill_panel", SkillPanelMenu::new);

    

    private static <C extends AbstractContainerMenu, T extends MenuType<C>> RegistryObject<T> register(String name,
            IContainerFactory<? extends C> f) {
        // noinspection unchecked
        return MENUS.register(name, () -> (T) IForgeMenuType.create(f));
    }
    // public static void register(IEventBus eventBus) {
    // MENUS.register(eventBus);
    // }
}
