package net.weyne1.easegui.client.animation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Util;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.api.animation.EasingType;

public final class AnimationSystem {

    private AnimationSystem() {}

    /**
     * Begins an animation scope using start timestamp and delay.
     * Use this overload for real-time game elements tied to a specific open time or start timestamp.
     *
     * @param startTime time in milliseconds when the screen/animation sequence started
     * @param delay delay in milliseconds before this specific element begins animating
     * @return active {@link AnimationScope}, or {@code null} if the duration has elapsed
     */
    public static AnimationScope begin(
            GuiGraphics graphics,
            AnimationProfile profile,
            int x,
            int y,
            int width,
            int height,
            long startTime,
            long delay,
            float baseAlpha
    ) {
        long elapsed = Util.getMillis() - startTime - delay;
        return begin(graphics, profile, x, y, width, height, elapsed, baseAlpha);
    }

    /**
     * Begins an animation scope using pre-calculated elapsed time.
     * Preferred when elapsed time is already calculated or adjusted for cascade delays.
     *
     * @param elapsed time passed since the element's animation start, in milliseconds
     * @return active {@link AnimationScope}, or {@code null} if the duration has elapsed
     */
    public static AnimationScope begin(
            GuiGraphics graphics,
            AnimationProfile profile,
            int x,
            int y,
            int width,
            int height,
            long elapsed,
            float baseAlpha
    ) {
        if (elapsed >= profile.getDuration()) return null;

        float rawProgress = elapsed <= 0 ? 0.0f : Math.min(1.0f, elapsed / (float) profile.getDuration());
        return begin(graphics, profile, x, y, width, height, rawProgress, baseAlpha);
    }

    /**
     * Core overload accepting linear raw progress [0.0..1.0].
     * Use this overload when driving animations from looped UI timers (e.g. editor preview).
     *
     * @param rawProgress linear progress between 0.0f and 1.0f. Do NOT pre-apply easing functions!
     * @return active {@link AnimationScope} context
     */
    public static AnimationScope begin(
            GuiGraphics graphics,
            AnimationProfile profile,
            int x,
            int y,
            int width,
            int height,
            float rawProgress,
            float baseAlpha
    ) {
        if (AnimationContext.isAnimationDisabled()) {
            return AnimationScope.NO_OP;
        }

        float clampedRaw = AnimationMath.clamp(rawProgress, 0.0f, 1.0f);
        float spatialProgress = profile.getEasing() != null ? profile.getEasing().ease(clampedRaw) : clampedRaw;

        float alphaProgress = EasingType.EASE_OUT_CUBIC.ease(clampedRaw);
        float lerpedAlpha = AnimationMath.lerp(profile.getInitialAlpha(), 1.0f, alphaProgress);
        float finalAlpha = AnimationMath.clamp(baseAlpha * lerpedAlpha, 0.0f, 1.0f);

        AnimationScope scope = new AnimationScope(graphics, finalAlpha);
        scope.pushTransforms(
                AnimationMath.calculateCurrentOffset(profile.getInitialOffsetX(), spatialProgress),
                AnimationMath.calculateCurrentOffset(profile.getInitialOffsetY(), spatialProgress),
                AnimationMath.lerp(profile.getInitialScaleX(), 1.0f, spatialProgress),
                AnimationMath.lerp(profile.getInitialScaleY(), 1.0f, spatialProgress),
                profile.getPivot().getX(x, width),
                profile.getPivot().getY(y, height)
        );
        return scope;
    }

    public static AnimationScope beginAlphaOnly(GuiGraphics graphics, float alpha) {
        if (AnimationContext.isAnimationDisabled()) {
            return AnimationScope.NO_OP;
        }
        return new AnimationScope(graphics, alpha);
    }
}