package com.zjw.moreskill.mixin.cooking;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(Item.class)
public class FoodItemMixin {

//    @Inject(method = "getFoodProperties" ,at = @At("HEAD"), cancellable = true)
//    private void onGetFood(CallbackInfoReturnable<FoodProperties> cir) {
//        FoodProperties returnValue = cir.getReturnValue();
//        // 如果返回值不为空（即当前物品是食物）
//        if (returnValue != null) {
//            // 创建一个新的 FoodProperties.Builder，基于原有的属性
//            FoodProperties.Builder builder = new FoodProperties.Builder()
//                    .nutrition(returnValue.getNutrition() + 4) // 增加饥饿值（例如 +4）
//                    .saturationMod(returnValue.getSaturationModifier() + 0.5F); // 增加饱和度（例如 +0.5）
//            // 保留原有的属性
//            if (returnValue.isMeat()) {
//                builder.meat(); // 如果是肉类，保留肉类属性
//            }
//            if (returnValue.canAlwaysEat()) {
//                builder.alwaysEat(); // 如果可以一直食用，保留该属性
//            }
//            if (returnValue.isFastFood()) {
//                builder.fast(); // 如果是快速食物，保留该属性
//            }
//            // 构建新的 FoodProperties
//            FoodProperties modifiedFood = builder.build();
//
//            // 返回修改后的 FoodProperties
//            cir.setReturnValue(modifiedFood);
//        }
//    }
}
