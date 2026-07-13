package net.weyne1.easegui.client.animation;

public final class AnimationMath {

    private AnimationMath() { }

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

    public static float calculateProgress(
            long elapsed,
            long duration,
            EasingType easing
    ) {
        if (elapsed <= 0) return 0f;
        if (elapsed >= duration) return 1f;

        float t = elapsed / (float) duration;
        return easing.ease(t);
    }
}