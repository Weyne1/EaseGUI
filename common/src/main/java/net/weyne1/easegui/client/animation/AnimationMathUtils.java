package net.weyne1.easegui.client.animation;

import net.weyne1.easegui.client.animation.AnimationProfile.PivotPoint;

public final class AnimationMathUtils {

    private AnimationMathUtils() {
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    public static float lerp(float start, float end, float progress) {
        return start + (end - start) * progress;
    }

    public static float calculateCurrentOffset(float baseOffset, float progress) {
        return baseOffset * (1.0f - progress);
    }

    public static float calculatePivotX(PivotPoint pivot, float x, float width) {
        return switch (pivot) {
            case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> x;
            case TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> x + width;
            default -> x + (width / 2f);
        };
    }

    public static float calculatePivotY(PivotPoint pivot, float y, float height) {
        return switch (pivot) {
            case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> y;
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> y + height;
            default -> y + (height / 2f);
        };
    }

    /**
     * Minecraft text rendering becomes unreliable below ~0.02 alpha,
     * causing glyphs to appear abruptly instead of fading smoothly.
     */
    public static float clampFontAlpha(float letterAlpha) {
        return clamp(letterAlpha, 0.02f, 1.0f);
    }

    public static float calculateProgress(
            long elapsed,
            long duration,
            AnimationProfile.EasingType easing
    ) {
        if (elapsed <= 0) return 0f;
        if (elapsed >= duration) return 1f;

        float t = elapsed / (float) duration;
        return easing.ease(t);
    }
}