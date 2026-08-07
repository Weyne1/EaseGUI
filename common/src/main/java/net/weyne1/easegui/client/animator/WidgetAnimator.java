package net.weyne1.easegui.client.animator;

import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.api.animation.AnimationDirection;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.animation.*;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class WidgetAnimator {

    public static AnimationScope beginRender(AbstractWidget widget, GuiGraphicsExtractor graphics, WidgetCategory category, WidgetAnimationState state) {
        if (Minecraft.getInstance().gui.screen() instanceof AbstractContainerScreen) {
            return null;
        }

        if (!ConfigManager.getConfig().global.enabled) {
            return null;
        }

        boolean isClosing = ScreenStateTracker.isClosing();
        AnimationDirection direction = isClosing ? AnimationDirection.OUT : AnimationDirection.IN;

        var profile = ConfigManager.getProfileForCurrentContext(category, direction);
        if (profile == null || !profile.isEnabled()) return null;

        long now = Util.getMillis();

        updateAnimationState(widget, state, now, profile, direction);

        if (ScreenStateTracker.isResizeFrame() || AnimationContext.hasParentAnimation()) {
            state.startTime = now - profile.getDuration() - state.delay;
            return null;
        }

        long actualStartTime = isClosing ? ScreenStateTracker.getClosingStartTime() : state.startTime;

        return AnimationSystem.begin(
                graphics,
                profile,
                direction,
                widget.getX(),
                widget.getY(),
                widget.getWidth(),
                widget.getHeight(),
                actualStartTime,
                state.delay,
                widget.getAlpha()
        );
    }

    private static void updateAnimationState(AbstractWidget widget, WidgetAnimationState state, long now, AnimationProfile profile, AnimationDirection direction) {
        int currentFrame = ScreenStateTracker.getCurrentFrameId();

        // Если сменилось направление (IN -> OUT) или пропущен кадр рендера — сбрасываем инициализацию
        if ((state.init && currentFrame > state.lastRenderFrame + 1) || state.lastDirection != direction) {
            state.init = false;
            state.lastDirection = direction;
        }

        if (!state.init) {
            state.init = true;

            if (direction == AnimationDirection.IN && !ScreenStateTracker.isClosing()) {
                state.startTime = ScreenStateTracker.getScreenOpenTime();
            } else {
                state.startTime = now;
            }

            float distance = getDistance(widget, profile);

            float delayMultiplier = profile.getCascadeDelay() / 100.0f;
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