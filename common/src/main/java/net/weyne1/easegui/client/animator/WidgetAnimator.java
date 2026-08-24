package net.weyne1.easegui.client.animator;

import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.animation.*;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class WidgetAnimator {

    public static AnimationScope beginWidget(AbstractWidget widget, GuiGraphicsExtractor graphics, WidgetCategory category, WidgetAnimationState state) {
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen) {
            return AnimationScope.NO_OP;
        }

        AnimationProfile profile = ConfigManager.getProfileForCurrentContext(category);
        long now = Util.getMillis();

        if (profile != null) {
            updateAnimationState(widget, state, now, profile);

            if (ScreenStateTracker.isResizeFrame() || AnimationContext.hasParentAnimation()) {
                state.startTime = now - profile.getDuration() - state.delay;
            }
        }

        return AnimationSystem.begin(graphics, profile, widget.getX(), widget.getY(),
                widget.getWidth(), widget.getHeight(), state.startTime, state.delay, widget.getAlpha());
    }

    private static void updateAnimationState(AbstractWidget widget, WidgetAnimationState state, long now, AnimationProfile profile) {
        int currentFrame = ScreenStateTracker.getCurrentFrameId();

        if (state.init && currentFrame > state.lastRenderFrame + 1) {
            state.init = false;
        }

        if (!state.init) {
            state.init = true;
            state.startTime = now;

            float distance = getDistance(widget, profile);
            float delayMultiplier = profile.getCascadeDelay() / 100.0f;
            state.delay = (long) (distance * delayMultiplier);
        }

        state.lastRenderFrame = currentFrame;
    }

    /**
     * Calculates the distance to the widget in a virtual coordinate scale,
     * making the cascade speed independent of the GUI scale and monitor resolution.
     */
    private static float getDistance(AbstractWidget widget, AnimationProfile profile) {
        var window = Minecraft.getInstance().getWindow();
        int screenHeight = window.getGuiScaledHeight();
        int screenWidth = window.getGuiScaledWidth();

        int x = widget.getX();
        int y = widget.getY();

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