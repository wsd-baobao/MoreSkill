package com.zjw.moreskill.entity;

import com.zjw.moreskill.MoreSkill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
            MoreSkill.MODID);

    public static final RegistryObject<EntityType<MoreSkillIronGolem>> MORE_SKILL_IRON_GOLEM = ENTITIES.register(
            "more_skill_iron_golem",
            () -> EntityType.Builder.of(MoreSkillIronGolem::new, MobCategory.MISC)
                    .sized(1.4F, 2.7F)
                    .clientTrackingRange(32).build("more_skill_iron_golem"));
}
