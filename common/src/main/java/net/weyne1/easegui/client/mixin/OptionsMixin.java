package net.weyne1.easegui.client.mixin;

import net.minecraft.client.Options;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.state.ScreenAnimationTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Options.class)
public class OptionsMixin {

    @Inject(method = "getMenuBackgroundBlurriness", at = @At("RETURN"), cancellable = true)
    private void easeGUI$animateMenuBlurriness(CallbackInfoReturnable<Integer> cir) {
        boolean enableSmoothBlur = ConfigManager.getConfig().global.enableSmoothBlur;

        if (!enableSmoothBlur || BackgroundAnimator.skipBackgroundFade) {
            return;
        }

        int originalValue = cir.getReturnValue();
        int animatedValue = Math.round(originalValue * ScreenAnimationTracker.getProgress());

        cir.setReturnValue(animatedValue);
    }
}