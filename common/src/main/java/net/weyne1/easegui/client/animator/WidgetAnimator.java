package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.weyne1.easegui.client.EaseGUIWidget;
import net.weyne1.easegui.client.animation.*;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.UIElementCategory;
import net.weyne1.easegui.client.state.ScreenStateTracker;

/**
 * Animates GUI widgets.
 */
public class WidgetAnimator {

    /**
     * Starts the animation.
     *
     * @return an {@link AnimationScope} that must be closed, or {@code null} if no animation is needed
     */
    public static AnimationScope beginRender(AbstractWidget widget, GuiGraphics gg, UIElementCategory category, AnimationState.AnimationData state) {
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen) {
            return null;
        }

        var profile = ConfigManager.getProfileForCurrentContext(category);
        if (profile == null || !profile.enabled) return null;

        long now = Util.getMillis();

        updateAnimationState(widget, state, now, profile);

        if (ScreenStateTracker.isResizeFrame() || AnimationContext.hasParentAnimation()) {
            state.startTime = now - profile.duration - state.delay;
            return null;
        }

        long elapsed = now - state.startTime - state.delay;

        if (elapsed >= profile.duration) return null;

        float progress = 0.0f;
        if (elapsed > 0) {
            progress = AnimationMath.calculateProgress(elapsed, profile.duration, profile.easing);
        }

        return AnimationSystem.begin(
                gg,
                widget.getX(),
                widget.getY(),
                widget.getWidth(),
                widget.getHeight(),
                profile,
                progress,
                ((EaseGUIWidget) widget).easeGUI$getAlpha()
        );
    }

    /**
     * Initializes animation state when a widget appears and
     * recalculates cascade timing if needed.
     */
    private static void updateAnimationState(AbstractWidget widget, AnimationState.AnimationData state, long now, AnimationProfile profile) {
        int currentFrame = ScreenStateTracker.getCurrentFrameId();

        if (state.init && currentFrame > state.lastRenderFrame + 1) {
            state.init = false;
        }

        if (!state.init) {
            state.init = true;
            state.startTime = now;

            float distance = getDistance(widget, profile);

            float delayMultiplier = profile.cascadeDelay / 100.0f;
            state.delay = (long) (distance * delayMultiplier);
        }

        state.lastRenderFrame = currentFrame;
    }

    /**
     * Вычисляет расстояние до виджета в виртуальной шкале координат,
     * делая скорость каскада независимой от GUI Scale и разрешения монитора.
     */
    private static float getDistance(AbstractWidget widget, AnimationProfile profile) {
        var window = Minecraft.getInstance().getWindow();
        int screenHeight = window.getGuiScaledHeight();
        int screenWidth = window.getGuiScaledWidth();

        int x = widget.getX();
        int y = widget.getY();

        final float BASELINE_WIDTH = 960.0f;
        final float BASELINE_HEIGHT = 540.0f;

        return switch (profile.cascadeDirection) {
            case TOP_TO_BOTTOM -> (Math.max(0, y) / (float) screenHeight) * BASELINE_HEIGHT;
            case BOTTOM_TO_TOP -> (Math.max(0f, screenHeight - y) / (float) screenHeight) * BASELINE_HEIGHT;
            case LEFT_TO_RIGHT -> (Math.max(0, x) / (float) screenWidth) * BASELINE_WIDTH;
            case RIGHT_TO_LEFT -> (Math.max(0f, screenWidth - x) / (float) screenWidth) * BASELINE_WIDTH;
        };
    }
}