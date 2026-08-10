package net.weyne1.easegui.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    public Screen screen;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void easeGUI$onScreenTransition(Screen guiScreen, CallbackInfo ci) {
        Screen oldScreen = this.screen;

        ScreenStateTracker.onScreenChange();

        if (!BackgroundAnimator.isBackgroundEffectAllowed(guiScreen)) {
            BackgroundAnimator.setSkipBackgroundAnimation(true);
            return;
        }

        boolean oldScreenWasActuallyShown = ScreenStateTracker.wasScreenRendered(oldScreen);
        boolean wasBlurred = oldScreenWasActuallyShown && ScreenStateTracker.wasLastScreenBlurred();

        BackgroundAnimator.setSkipBackgroundAnimation(wasBlurred);
    }
}