package net.weyne1.easegui.client.animation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Util;

public final class AnimationSystem {

    /**
     * An entry point for animations with automatic timing and progress calculation.
     */
    public static AnimationScope begin(
            GuiGraphics gg,
            int x, int y, int width, int height,
            AnimationProfile profile,
            long startTime,
            long delay,
            float baseAlpha
    ) {
        long elapsed = Util.getMillis() - startTime - delay;

        if (elapsed >= profile.duration) {
            return null;
        }

        float progress = elapsed <= 0 ? 0.0f : AnimationMath.calculateProgress(elapsed, profile.duration, profile.easing);

        return begin(gg, x, y, width, height, profile, progress, baseAlpha);
    }

    public static AnimationScope begin(
            GuiGraphics gg,
            int x, int y, int width, int height,
            AnimationProfile profile,
            float progress,
            float baseAlpha
    ) {
        float alphaProgress = AnimationMath.clamp(progress, 0.0f, 1.0f);
        float lerpedAlpha = AnimationMath.lerp(profile.startAlpha, 1.0f, alphaProgress);
        float finalAlpha = AnimationMath.clamp(baseAlpha * lerpedAlpha, 0.0f, 1.0f);

        AnimationScope scope = new AnimationScope(gg, finalAlpha);
        scope.setTransformParams(
                AnimationMath.calculateCurrentOffset(profile.offset.x, progress),
                AnimationMath.calculateCurrentOffset(profile.offset.y, progress),
                AnimationMath.lerp(profile.startScale.x, 1.0f, progress),
                AnimationMath.lerp(profile.startScale.y, 1.0f, progress),
                profile.pivot.getX(x, width),
                profile.pivot.getY(y, height)
        );
        return scope;
    }

    public static AnimationScope beginAlphaOnly(GuiGraphics gg, float alpha) {
        return new AnimationScope(gg, alpha);
    }
}