package com.zjw.moreskill.skill.smithing;


import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class SmithingDurabilityMixin {

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    private void applyDurabilityModifiers(CallbackInfoReturnable<Integer> cir) {
        int original = cir.getReturnValue();
        // Apply your custom logic here
        cir.setReturnValue(original + 1000); // Example modification
    }
}
