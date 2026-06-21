package net.weyne1.easegui.client.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.animator.ContainerAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects custom animation behavior into Screen rendering pipeline.
 * Handles:
 * - animated background gradients
 * - animated menu backgrounds
 * - container widget rendering transitions
 */
@Mixin(Screen.class)
public class ScreenMixin {

    @SuppressWarnings("ConstantValue")
    @Unique
    private boolean easeGUI$isWorldLoadingScreen() {
        Object self = this;
        return self instanceof LevelLoadingScreen || self instanceof ProgressScreen;
    }

    /**
     * Applies animated alpha to the top color of Screen background gradient.
     */
    @ModifyArg(
            method = "renderTransparentBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"),
            index = 4
    )
    private int easeGUI$modifyTransparentBgTopColor(int originalColor) {
        if (easeGUI$isWorldLoadingScreen()) {
            return originalColor;
        }
        return BackgroundAnimator.getAnimatedColor(originalColor);
    }

    /**
     * Applies animated alpha to the bottom color of Screen background gradient.
     */
    @ModifyArg(
            method = "renderTransparentBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"),
            index = 5
    )
    private int easeGUI$modifyTransparentBgBottomColor(int originalColor) {
        if (easeGUI$isWorldLoadingScreen()) {
            return originalColor;
        }
        return BackgroundAnimator.getAnimatedColor(originalColor);
    }

    /**
     * Prepares animation state before rendering menu background texture.
     */
    @Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"))
    private void easeGUI$preRenderMenuBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (!easeGUI$isWorldLoadingScreen() && BackgroundAnimator.shouldAnimate()) {
            BackgroundAnimator.preRenderMenu(guiGraphics);
        }
    }

    @Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("RETURN"))
    private void easeGUI$postRenderMenuBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (!easeGUI$isWorldLoadingScreen() && BackgroundAnimator.shouldAnimate()) {
            BackgroundAnimator.postRenderMenu(guiGraphics);
        }
    }
}