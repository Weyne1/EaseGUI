package net.weyne1.easegui.client.mixin.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.state.ScreenOutDurationCalculator;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow private Screen screen;

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void easeGUI$onScreenTransition(Screen screen, CallbackInfo ci) {
        if (ScreenStateTracker.isBypassInterceptor()) {
            ScreenStateTracker.markScreenOpened();
            return;
        }

        Screen oldScreen = this.screen;

        if (ScreenStateTracker.isClosing()) {
            if (screen != oldScreen) {
                ScreenStateTracker.setPendingScreen(screen);
            }
            ci.cancel();
            return;
        }

        if (oldScreen != null && oldScreen != screen) {
            long maxOutTime = ScreenOutDurationCalculator.calculateMaxOutDuration(oldScreen);
            if (maxOutTime > 0) {
                boolean started = ScreenStateTracker.startClosingProcedure(screen, maxOutTime);
                if (started) {
                    ci.cancel();
                    return;
                }
            }
        }

        ScreenStateTracker.markScreenOpened();

        if (!BackgroundAnimator.shouldAnimateBackground(screen)) {
            BackgroundAnimator.skipBackgroundFade = true;
            return;
        }

        boolean wasBlurred = BackgroundAnimator.shouldAnimateBackground(oldScreen);
        boolean willBeBlurred = BackgroundAnimator.shouldAnimateBackground(screen);

        BackgroundAnimator.skipBackgroundFade = wasBlurred && willBeBlurred;
    }
}