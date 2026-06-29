package net.weyne1.easegui.client.animator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.*;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.config.EaseGUIScreenRegistry;
import net.weyne1.easegui.client.config.ScreenType;
import net.weyne1.easegui.client.state.ScreenAnimationTracker;
import net.weyne1.easegui.client.state.ScreenStateTracker;

/**
 * Handles background fade animations.
 */
public class BackgroundAnimator {
    public static boolean skipBackgroundFade = false;

    public static boolean isLoadingScreen(Screen screen) {
        return screen instanceof LevelLoadingScreen
                || screen instanceof ProgressScreen
                || screen instanceof ConnectScreen
                || screen instanceof ReceivingLevelScreen
                || screen instanceof GenericWaitingScreen
                || screen instanceof BackupConfirmScreen;
    }

    public static boolean isScreenBlurred(Screen screen) {
        if (screen == null || isLoadingScreen(screen)) return false;

        EaseGUIConfig config = ConfigManager.getConfig();
        if (!config.global.enableSmoothDimming) return false;

        try {
            ScreenType screenType = EaseGUIScreenRegistry.from(screen);
            if (screenType == null) return true;

            EaseGUIConfig.ScreenSettings screenSettings = config.screens.get(screenType.getId());
            return screenSettings == null || screenSettings.enabled;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean shouldAnimate() {
        return isScreenBlurred(Minecraft.getInstance().screen);
    }

    /**
     * Returns whether background animations are enabled.
     */
    public static int getAnimatedColor(int originalColor) {
        if (!shouldAnimate() || skipBackgroundFade) {
            return originalColor;
        }

        long elapsed = ScreenStateTracker.getScreenElapsed();
        long duration = ConfigManager.getConfig().global.dimmingDuration;
        if (elapsed >= duration) {
            return originalColor;
        }

        float progress = Math.max(0.0f, Math.min(1.0f, ScreenAnimationTracker.getProgress()));
        int originalAlpha = (originalColor >> 24) & 0xFF;
        int finalAlpha = (int) (originalAlpha * progress);

        return (originalColor & 0x00FFFFFF) | (finalAlpha << 24);
    }

    /**
     * Starts the animation.
     *
     * @return an {@link AnimationScope} that must be closed, or {@code null} if no animation is needed
     */
    public static AnimationScope beginRenderMenu(GuiGraphics gg) {
        if (!shouldAnimate() || skipBackgroundFade) return null;

        long elapsed = ScreenStateTracker.getScreenElapsed();
        long duration = ConfigManager.getConfig().global.dimmingDuration;
        if (elapsed >= duration) {
            return null;
        }

        float progress = Math.max(0.0f, Math.min(1.0f, ScreenAnimationTracker.getProgress()));
        return AnimationSystem.beginAlphaOnly(gg, progress);
    }
}