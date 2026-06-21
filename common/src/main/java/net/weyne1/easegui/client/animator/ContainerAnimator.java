package net.weyne1.easegui.client.animator;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.Util;
import net.weyne1.easegui.client.animation.AnimationEngine;
import net.weyne1.easegui.client.animation.AnimationMathUtils;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import net.weyne1.easegui.client.config.UIElementCategory;

/**
 * Animates container screens.
 */
public class ContainerAnimator {

    public static boolean preRender(AbstractContainerScreen<?> screen, GuiGraphics gg) {
        var profile = ConfigManager.getProfileForCurrentContext(UIElementCategory.CONTAINERS);
        if (profile == null || !profile.enabled) return false;

        long startTime = ScreenStateTracker.getScreenOpenTime();
        
        long elapsed = Util.getMillis() - startTime;
        float progress = AnimationMathUtils.calculateProgress(elapsed, profile.duration, profile.easing);

        AnimationEngine.apply(gg, 0, 0, screen.width, screen.height, profile, progress, 1.0f);

        return true;
    }

    public static void postRender(GuiGraphics gg) {
        AnimationEngine.cleanUp(gg);
    }
}