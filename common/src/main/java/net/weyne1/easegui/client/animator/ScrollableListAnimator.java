package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationState;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.state.ScreenStateTracker;

/**
 * Animates AbstractSelectionList instances, which are Renderable but not
 * AbstractWidget instances in Minecraft 1.20.1.
 */
public final class ScrollableListAnimator {
    private ScrollableListAnimator() {
    }

    public static AnimationScope beginRender(GuiGraphics gg, int x, int y, int width, int height, AnimationState state) {
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen) {
            return null;
        }

        if (!ConfigManager.getConfig().global.enabled) {
            return null;
        }

        AnimationProfile profile = ConfigManager.getProfileForCurrentContext(WidgetCategory.SCROLLABLE);
        if (profile == null || !profile.isEnabled()) {
            return null;
        }

        long now = Util.getMillis();
        updateAnimationState(x, y, state, now, profile);

        if (ScreenStateTracker.isResizeFrame() || AnimationContext.hasParentAnimation()) {
            state.startTime = now - profile.getDuration() - state.delay;
            return null;
        }

        return AnimationSystem.begin(gg, x, y, width, height, profile, state.startTime, state.delay, 1.0f);
    }

    private static void updateAnimationState(int x, int y, AnimationState state, long now, AnimationProfile profile) {
        int currentFrame = ScreenStateTracker.getCurrentFrameId();

        if (state.init && currentFrame > state.lastRenderFrame + 1) {
            state.init = false;
        }

        if (!state.init) {
            state.init = true;
            state.startTime = now;

            float distance = getDistance(x, y, profile);
            float delayMultiplier = profile.getCascadeDelay() / 100.0f;
            state.delay = (long) (distance * delayMultiplier);
        }

        state.lastRenderFrame = currentFrame;
    }

    private static float getDistance(int x, int y, AnimationProfile profile) {
        var window = Minecraft.getInstance().getWindow();
        int screenHeight = window.getGuiScaledHeight();
        int screenWidth = window.getGuiScaledWidth();

        final float BASELINE_WIDTH = 960.0f;
        final float BASELINE_HEIGHT = 540.0f;

        return switch (profile.getCascadeDirection()) {
            case TOP_TO_BOTTOM -> (Math.max(0, y) / (float) screenHeight) * BASELINE_HEIGHT;
            case BOTTOM_TO_TOP -> (Math.max(0f, screenHeight - y) / (float) screenHeight) * BASELINE_HEIGHT;
            case LEFT_TO_RIGHT -> (Math.max(0, x) / (float) screenWidth) * BASELINE_WIDTH;
            case RIGHT_TO_LEFT -> (Math.max(0f, screenWidth - x) / (float) screenWidth) * BASELINE_WIDTH;
        };
    }
}