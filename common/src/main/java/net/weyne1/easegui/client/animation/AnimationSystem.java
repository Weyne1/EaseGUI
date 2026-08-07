package net.weyne1.easegui.client.animation;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Util;
import net.weyne1.easegui.api.animation.AnimationDirection;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.api.animation.EasingType;

public final class AnimationSystem {

    private AnimationSystem() {}

    public static AnimationScope begin(
            GuiGraphicsExtractor graphics,
            AnimationProfile profile,
            AnimationDirection direction,
            int x,
            int y,
            int width,
            int height,
            long startTime,
            long delay,
            float baseAlpha
    ) {
        long elapsed = Util.getMillis() - startTime - delay;
        return begin(graphics, profile, direction, x, y, width, height, elapsed, baseAlpha);
    }

    public static AnimationScope begin(
            GuiGraphicsExtractor graphics,
            AnimationProfile profile,
            AnimationDirection direction,
            int x,
            int y,
            int width,
            int height,
            long elapsed,
            float baseAlpha
    ) {
        if (elapsed >= profile.getDuration()) {
            if (direction.isOut()) {
                return beginFullyExited(graphics, profile, x, y, width, height, baseAlpha);
            }
            return null;
        }

        float rawProgress = elapsed <= 0 ? 0.0f : Math.min(1.0f, elapsed / (float) profile.getDuration());
        return begin(graphics, profile, direction, x, y, width, height, rawProgress, baseAlpha);
    }

    public static AnimationScope begin(
            GuiGraphicsExtractor graphics,
            AnimationProfile profile,
            AnimationDirection direction,
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

        boolean isOut = direction.isOut();
        float alphaProgress = isOut ? EasingType.EASE_IN_CUBIC.ease(clampedRaw) : EasingType.EASE_OUT_CUBIC.ease(clampedRaw);

        float currentOffsetPxX;
        float currentOffsetPxY;
        float currentScaleX;
        float currentScaleY;
        float lerpedAlpha;

        if (!isOut) {
            currentOffsetPxX = AnimationMath.lerp(profile.getInitialOffsetX(), 0.0f, spatialProgress);
            currentOffsetPxY = AnimationMath.lerp(profile.getInitialOffsetY(), 0.0f, spatialProgress);
            currentScaleX = AnimationMath.lerp(profile.getInitialScaleX(), 1.0f, spatialProgress);
            currentScaleY = AnimationMath.lerp(profile.getInitialScaleY(), 1.0f, spatialProgress);
            lerpedAlpha = AnimationMath.lerp(profile.getInitialAlpha(), 1.0f, alphaProgress);
        } else {
            currentOffsetPxX = AnimationMath.lerp(0.0f, profile.getInitialOffsetX(), spatialProgress);
            currentOffsetPxY = AnimationMath.lerp(0.0f, profile.getInitialOffsetY(), spatialProgress);
            currentScaleX = AnimationMath.lerp(1.0f, profile.getInitialScaleX(), spatialProgress);
            currentScaleY = AnimationMath.lerp(1.0f, profile.getInitialScaleY(), spatialProgress);
            lerpedAlpha = AnimationMath.lerp(1.0f, profile.getInitialAlpha(), alphaProgress);
        }

        float finalAlpha = AnimationMath.clamp(baseAlpha * lerpedAlpha, 0.0f, 1.0f);

        AnimationScope scope = new AnimationScope(graphics, finalAlpha);
        scope.pushTransforms(
                currentOffsetPxX,
                currentOffsetPxY,
                currentScaleX,
                currentScaleY,
                profile.getPivot().getX(x, width),
                profile.getPivot().getY(y, height)
        );
        return scope;
    }

    private static AnimationScope beginFullyExited(
            GuiGraphicsExtractor graphics,
            AnimationProfile profile,
            int x, int y, int width, int height,
            float baseAlpha
    ) {
        if (AnimationContext.isAnimationDisabled()) {
            return AnimationScope.NO_OP;
        }

        float finalAlpha = AnimationMath.clamp(baseAlpha * profile.getInitialAlpha(), 0.0f, 1.0f);
        AnimationScope scope = new AnimationScope(graphics, finalAlpha);
        scope.pushTransforms(
                profile.getInitialOffsetX(),
                profile.getInitialOffsetY(),
                profile.getInitialScaleX(),
                profile.getInitialScaleY(),
                profile.getPivot().getX(x, width),
                profile.getPivot().getY(y, height)
        );
        return scope;
    }

    public static AnimationScope beginAlphaOnly(GuiGraphicsExtractor graphics, float alpha) {
        if (AnimationContext.isAnimationDisabled()) {
            return AnimationScope.NO_OP;
        }
        return new AnimationScope(graphics, alpha);
    }
}