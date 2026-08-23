package net.weyne1.easegui.client.mixin.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow private Screen screen;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void easegui$onScreenTransition(Screen screen, CallbackInfo ci) {
        Screen oldScreen = this.screen;

        ScreenStateTracker.onScreenTransition(oldScreen, screen);

        if (!BackgroundAnimator.isBackgroundEffectAllowed(screen)) {
            BackgroundAnimator.setSkipBackgroundAnimation(true);
            return;
        }

        BackgroundAnimator.setSkipBackgroundAnimation(ScreenStateTracker.wasPreviousScreenBlurred());
    }
}