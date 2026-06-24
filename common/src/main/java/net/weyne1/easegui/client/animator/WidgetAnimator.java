package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.weyne1.easegui.client.animation.AnimationEngine;
import net.weyne1.easegui.client.animation.AnimationMathUtils;
import net.weyne1.easegui.client.state.EaseGUIState;
import net.weyne1.easegui.client.EaseGUIWidget;
import net.weyne1.easegui.client.config.UIElementCategory;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.state.ScreenStateTracker;

/**
 * Animates GUI widgets.
 */
public class WidgetAnimator {

    public static boolean preRender(AbstractWidget widget, GuiGraphics gg, UIElementCategory category, EaseGUIState.AnimationData state) {
        var profile = ConfigManager.getProfileForCurrentContext(category);
        if (profile == null || !profile.enabled) return false;

        long now = Util.getMillis();
        updateAnimationState(widget, state, now, profile);

        long elapsed = now - state.startTime - state.delay;
        float progress = AnimationMathUtils.calculateProgress(elapsed, profile.duration, profile.easing);

        AnimationEngine.apply(gg, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), profile, progress, state.baseAlpha);

        return true;
    }

    public static void postRender(GuiGraphics gg) {
        AnimationEngine.cleanUp(gg);
    }

    /**
     * Initializes animation state when a widget appears and
     * recalculates cascade timing if needed.
     */
    private static void updateAnimationState(AbstractWidget widget, EaseGUIState.AnimationData state, long now, AnimationProfile profile) {
        int currentFrame = ScreenStateTracker.getCurrentFrameId();

        if (state.init && currentFrame > state.lastRenderFrame + 1) {
            state.init = false;
        }

        if (!state.init) {
            state.init = true;
            state.startTime = now;
            state.baseAlpha = ((EaseGUIWidget) widget).easeGUI$getAlpha();

            float distance = getDistance(widget, profile);

            float delayMultiplier = profile.cascadeDelay / 100.0f;
            state.delay = (long) (distance * delayMultiplier);
        }

        state.lastRenderFrame = currentFrame;
    }

    private static float getDistance(AbstractWidget widget, AnimationProfile profile) {
        var window = Minecraft.getInstance().getWindow();
        int screenHeight = window.getGuiScaledHeight();
        int screenWidth = window.getGuiScaledWidth();

        int x = widget.getX();
        int y = widget.getY();

        return switch (profile.cascadeDirection) {
            case TOP_TO_BOTTOM -> y;
            case BOTTOM_TO_TOP -> Math.max(0f, screenHeight - y);
            case LEFT_TO_RIGHT -> x;
            case RIGHT_TO_LEFT -> Math.max(0f, screenWidth - x);
        };
    }
}