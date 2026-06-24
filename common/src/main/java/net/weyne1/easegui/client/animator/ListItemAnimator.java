package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.animation.AnimationEngine;
import net.weyne1.easegui.client.animation.AnimationMathUtils;
import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.config.UIElementCategory;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.state.ScreenStateTracker;

/**
 * Animates list entries using cascade timing.
 */
public class ListItemAnimator {

    public static boolean preRender(GuiGraphics gg, int top, int left, int width, int height) {
        var profile = ConfigManager.getProfileForCurrentContext(UIElementCategory.LIST_ENTRY);
        if (profile == null || !profile.enabled) return false;

        long delay = getDelay(top, left, profile);
        long startTime = ScreenStateTracker.getScreenOpenTime();

        long elapsed = Util.getMillis() - startTime - delay;
        float progress = AnimationMathUtils.calculateProgress(elapsed, profile.duration, profile.easing);

        AnimationEngine.apply(gg, left, top, width, height, profile, progress, 1.0f);

        return true;
    }

    public static void postRender(GuiGraphics gg) {
        AnimationEngine.cleanUp(gg);
    }

    private static long getDelay(int top, int left, AnimationProfile profile) {
        var window = Minecraft.getInstance().getWindow();
        int screenHeight = window.getGuiScaledHeight();
        int screenWidth = window.getGuiScaledWidth();

        float distance = switch (profile.cascadeDirection) {
            case TOP_TO_BOTTOM -> top;
            case BOTTOM_TO_TOP -> Math.max(0f, screenHeight - top);
            case LEFT_TO_RIGHT -> left;
            case RIGHT_TO_LEFT -> Math.max(0f, screenWidth - left);
        };

        return (long) (distance * (profile.cascadeDelay / 100.0f));
    }
}