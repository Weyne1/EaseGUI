package net.weyne1.easegui.client.animator;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.state.BackgroundAnimationTracker;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BackgroundAnimator {
    private static boolean skipBackgroundAnimation = false;

    private static final Set<Class<? extends Screen>> IGNORED_SCREEN_CLASSES = ConcurrentHashMap.newKeySet();

    @SuppressWarnings("unused")
    public static void registerIgnoredScreen(Class<? extends Screen> screenClass) {
        IGNORED_SCREEN_CLASSES.add(screenClass);
    }

    public static boolean isBackgroundEffectAllowed(Screen screen) {
        if (AnimationContext.isAnimationDisabled()) return false;
        return screen != null && !isIgnoredScreen(screen);
    }

    public static boolean isBackgroundAnimationSkipped() {
        return skipBackgroundAnimation;
    }

    public static void setSkipBackgroundAnimation(boolean skip) {
        skipBackgroundAnimation = skip;
    }

    public static int getAnimatedColor(Screen screen, int originalColor) {
        if (!isBackgroundEffectAllowed(screen)) {
            return originalColor;
        }

        float progress = BackgroundAnimationTracker.getProgress();
        int originalAlpha = (originalColor >> 24) & 0xFF;
        int finalAlpha = Math.round(originalAlpha * progress);

        return (originalColor & 0x00FFFFFF) | (finalAlpha << 24);
    }

    public static AnimationScope beginRenderMenu(Screen screen, GuiGraphics graphics) {
        if (!isBackgroundEffectAllowed(screen)) {
            return null;
        }

        float progress = BackgroundAnimationTracker.getProgress();
        if (progress >= 1.0f) {
            return null;
        }

        return AnimationSystem.beginAlphaOnly(graphics, progress);
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