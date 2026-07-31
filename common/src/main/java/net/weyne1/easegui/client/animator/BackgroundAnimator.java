package net.weyne1.easegui.client.animator;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.*;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.api.EaseGUIScreenRegistry;
import net.weyne1.easegui.api.EaseGUIScreenType;
import net.weyne1.easegui.client.state.ScreenAnimationTracker;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BackgroundAnimator {
    public static boolean skipBackgroundFade = false;

    private static final Set<Class<? extends Screen>> IGNORED_SCREEN_CLASSES = ConcurrentHashMap.newKeySet();

    static {
        IGNORED_SCREEN_CLASSES.add(TitleScreen.class);
        IGNORED_SCREEN_CLASSES.add(LevelLoadingScreen.class);
        IGNORED_SCREEN_CLASSES.add(ProgressScreen.class);
        IGNORED_SCREEN_CLASSES.add(ConnectScreen.class);
        IGNORED_SCREEN_CLASSES.add(GenericWaitingScreen.class);
        IGNORED_SCREEN_CLASSES.add(BackupConfirmScreen.class);
    }

    @SuppressWarnings("unused")
    public static void registerIgnoredScreen(Class<? extends Screen> screenClass) {
        IGNORED_SCREEN_CLASSES.add(screenClass);
    }

    public static boolean shouldAnimateBackground(Screen screen) {
        if (AnimationContext.isAnimationDisabled()) {
            return false;
        }

        if (screen == null || isIgnoredScreen(screen)) {
            return false;
        }

        EaseGUIConfig config = ConfigManager.getConfig();
        if (!config.global.enableSmoothBlur) return false;

        try {
            EaseGUIScreenType screenType = EaseGUIScreenRegistry.from(screen);
            if (screenType == null) return true;

            EaseGUIConfig.ScreenSettings screenSettings = config.screens.get(screenType.getId());
            return screenSettings == null || screenSettings.enabled;
        } catch (Exception e) {
            return true;
        }
    }

    public static int getAnimatedColor(Screen screen, int originalColor) {
        if (!shouldAnimateBackground(screen) || skipBackgroundFade) {
            return originalColor;
        }

        long elapsed = ScreenStateTracker.getScreenElapsed();
        long duration = ConfigManager.getConfig().global.blurDuration;
        if (elapsed >= duration) {
            return originalColor;
        }

        float progress = Math.max(0.0f, Math.min(1.0f, ScreenAnimationTracker.getProgress()));
        int originalAlpha = (originalColor >> 24) & 0xFF;
        int finalAlpha = (int) (originalAlpha * progress);

        return (originalColor & 0x00FFFFFF) | (finalAlpha << 24);
    }

    public static AnimationScope beginRenderMenu(Screen screen, GuiGraphics gg) {
        if (!shouldAnimateBackground(screen) || skipBackgroundFade) {
            return null;
        }

        long elapsed = ScreenStateTracker.getScreenElapsed();
        long duration = ConfigManager.getConfig().global.blurDuration;
        if (elapsed >= duration) {
            return null;
        }

        float progress = Math.max(0.0f, Math.min(1.0f, ScreenAnimationTracker.getProgress()));
        return AnimationSystem.beginAlphaOnly(gg, progress);
    }

    private static boolean isIgnoredScreen(Screen screen) {
        Class<? extends Screen> screenClass = screen.getClass();
        for (Class<? extends Screen> ignored : IGNORED_SCREEN_CLASSES) {
            if (ignored.isAssignableFrom(screenClass)) {
                return true;
            }
        }
        return false;
    }
}