package net.weyne1.easegui.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
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

        if (BackgroundAnimator.isLoadingScreen(guiScreen)) {
            BackgroundAnimator.skipBackgroundFade = true;
            return;
        }

        boolean wasBlurred = BackgroundAnimator.isScreenBlurred(oldScreen);
        boolean willBeBlurred = BackgroundAnimator.isScreenBlurred(guiScreen);

        BackgroundAnimator.skipBackgroundFade = wasBlurred && willBeBlurred;
    }
}