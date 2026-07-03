package net.weyne1.easegui.client.animation;

import net.minecraft.client.gui.GuiGraphics;

public final class AnimationSystem {

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
                AnimationMath.calculateCurrentOffset(profile.offsetX, progress),
                AnimationMath.calculateCurrentOffset(profile.offsetY, progress),
                AnimationMath.lerp(profile.startScaleX, 1.0f, progress),
                AnimationMath.lerp(profile.startScaleY, 1.0f, progress),
                profile.pivot.getX(x, width),
                profile.pivot.getY(y, height)
        );
        return scope;
    }

    public static AnimationScope beginAlphaOnly(GuiGraphics gg, float alpha) {
        return new AnimationScope(gg, alpha);
    }
}