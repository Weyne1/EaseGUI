package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.weyne1.easegui.client.animation.AnimationEngine;
import net.weyne1.easegui.client.animation.AnimationMathUtils;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.ScreenRegistry;
import net.weyne1.easegui.client.state.RenderContext;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import net.weyne1.easegui.client.config.ScreenType;

/**
 * Animates the advancements screen.
 */
public class AdvancementsAnimator {

    /**
     * Applies animation to the main advancements window.
     */
    public static boolean preRenderWindow(AdvancementsScreen screen, GuiGraphics gg) {
        ScreenType type = ScreenRegistry.from(screen);
        var titleSettings = ConfigManager.getConfig().screens.get(type.getId());

        if (titleSettings == null || !titleSettings.enabled || titleSettings.advancements == null || !titleSettings.advancements.windowProfile.enabled) {
            return false;
        }

        var profile = titleSettings.advancements.windowProfile;
        long startTime = ScreenStateTracker.getScreenOpenTime();

        long elapsed = Util.getMillis() - startTime;
        float progress = AnimationMathUtils.calculateProgress(elapsed, profile.duration, profile.easing);

        AnimationEngine.apply(gg, 0, 0, screen.width, screen.height, profile, progress, 1.0f);
        return true;
    }

    public static void postRenderWindow(GuiGraphics gg) {
        AnimationEngine.cleanUp(gg);
    }

    /**
     * Applies animation to an advancement tab.
     */
    public static boolean preRenderTab(Screen screen, GuiGraphics gg, int tabIndex) {
        ScreenType type = ScreenRegistry.from(screen);
        var titleSettings = ConfigManager.getConfig().screens.get(type.getId());

        if (titleSettings == null || !titleSettings.enabled || titleSettings.advancements == null || !titleSettings.advancements.tabsProfile.enabled) {
            return false;
        }

        // First tab is not animated to avoid artifacts.
        if (tabIndex == 0) {
            return false;
        }

        var profile = titleSettings.advancements.tabsProfile;
        long startTime = ScreenStateTracker.getScreenOpenTime();
        long tabDelay = tabIndex * profile.cascadeDelay;

        float parentAlpha = RenderContext.getCurrentAlpha();

        long elapsed = Util.getMillis() - startTime - tabDelay;
        float progress = AnimationMathUtils.calculateProgress(elapsed, profile.duration, profile.easing);

        AnimationEngine.apply(gg, 0, 0, 28, 32, profile, progress, parentAlpha);
        return true;
    }

    public static void postRenderTab(GuiGraphics gg) {
        AnimationEngine.cleanUp(gg);
    }
}