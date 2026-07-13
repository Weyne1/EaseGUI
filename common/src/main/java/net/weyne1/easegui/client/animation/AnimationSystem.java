package net.weyne1.easegui.client.animation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.Util;
import net.weyne1.easegui.api.animation.AnimationProfile;

public final class AnimationSystem {

    public static AnimationScope begin(
            GuiGraphics gg,
            int x, int y, int width, int height,
            AnimationProfile profile,
            long startTime,
            long delay,
            float baseAlpha
    ) {
        long elapsed = Util.getMillis() - startTime - delay;

        if (elapsed >= profile.getDuration()) {
            return null;
        }

        float progress = elapsed <= 0 ? 0.0f : AnimationMath.calculateProgress(elapsed, profile.getDuration(), profile.getEasing());

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
        float lerpedAlpha = AnimationMath.lerp(profile.getStartAlpha(), 1.0f, alphaProgress);
        float finalAlpha = AnimationMath.clamp(baseAlpha * lerpedAlpha, 0.0f, 1.0f);

        AnimationScope scope = new AnimationScope(gg, finalAlpha);
        scope.setTransformParams(
                AnimationMath.calculateCurrentOffset(profile.getOffsetX(), progress),
                AnimationMath.calculateCurrentOffset(profile.getOffsetY(), progress),
                AnimationMath.lerp(profile.getStartScaleX(), 1.0f, progress),
                AnimationMath.lerp(profile.getStartScaleY(), 1.0f, progress),
                profile.getPivot().getX(x, width),
                profile.getPivot().getY(y, height)
        );
        return scope;
    }

    public static AnimationScope beginAlphaOnly(GuiGraphics gg, float alpha) {
        return new AnimationScope(gg, alpha);
    }
}