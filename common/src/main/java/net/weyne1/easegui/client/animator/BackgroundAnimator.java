package net.weyne1.easegui.client.animator;

import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.animation.AnimationEngine;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.state.ScreenAnimationTracker;

/**
 * Handles background fade animations.
 */
public class BackgroundAnimator {

    public static boolean shouldAnimate() {
        return ConfigManager.getConfig().global.enableSmoothBlur;
    }

    /**
     * Returns whether background animations are enabled.
     */
    public static int getAnimatedColor(int originalColor) {
        if (!shouldAnimate()) {
            return originalColor;
        }

        float progress = ScreenAnimationTracker.getProgress();
        int originalAlpha = (originalColor >> 24) & 0xFF;
        int finalAlpha = (int) (originalAlpha * progress);

        return (originalColor & 0x00FFFFFF) | (finalAlpha << 24);
    }

    public static void preRenderMenu(GuiGraphics gg) {
        float progress = ScreenAnimationTracker.getProgress();
        AnimationEngine.applyAlphaOnly(gg, progress);
    }

    public static void postRenderMenu(GuiGraphics gg) {
        AnimationEngine.cleanUp(gg);
    }
}