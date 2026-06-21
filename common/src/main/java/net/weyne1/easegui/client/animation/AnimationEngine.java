package net.weyne1.easegui.client.animation;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.state.RenderContext;

/**
 * Applies animation transforms and alpha blending during widget rendering.
 */
public final class AnimationEngine {

    private AnimationEngine() {
    }

    /**
     * Applies animation transforms and alpha blending.
     * Must be paired with {@link #cleanUp(GuiGraphics)}.
     */
    public static void apply(
            GuiGraphics gg,
            int x,
            int y,
            int width,
            int height,
            AnimationProfile profile,
            float progress,
            float baseAlpha
    ) {
        gg.pose().pushPose();

        float alphaProgress = AnimationMathUtils.clamp(progress, 0.0f, 1.0f);
        float lerpedAlpha = AnimationMathUtils.lerp(profile.startAlpha, 1.0f, alphaProgress);
        float finalAlpha = baseAlpha * AnimationMathUtils.clampFontAlpha(lerpedAlpha);

        RenderContext.startWidgetAnimation(finalAlpha);
        gg.setColor(1.0f, 1.0f, 1.0f, finalAlpha);

        if (finalAlpha < 1.0f) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }

        float offsetX = AnimationMathUtils.calculateCurrentOffset(profile.offsetX, progress);
        float offsetY = AnimationMathUtils.calculateCurrentOffset(profile.offsetY, progress);

        float scaleX = AnimationMathUtils.lerp(profile.startScaleX, 1.0f, progress);
        float scaleY = AnimationMathUtils.lerp(profile.startScaleY, 1.0f, progress);

        if (scaleX != 1.0f || scaleY != 1.0f) {
            float pivotX = AnimationMathUtils.calculatePivotX(profile.pivot, x, width);
            float pivotY = AnimationMathUtils.calculatePivotY(profile.pivot, y, height);

            gg.pose().translate(offsetX + pivotX, offsetY + pivotY, 0.0f);
            gg.pose().scale(scaleX, scaleY, 1.0f);
            gg.pose().translate(-pivotX, -pivotY, 0.0f);
        } else {
            gg.pose().translate(offsetX, offsetY, 0.0f);
        }
    }

    /**
     * Applies alpha blending only.
     * Must be paired with {@link #cleanUp(GuiGraphics)}.
     */
    public static void applyAlphaOnly(GuiGraphics gg, float alpha) {
        gg.pose().pushPose();

        RenderContext.startWidgetAnimation(alpha);
        gg.setColor(1.0f, 1.0f, 1.0f, alpha);

        if (alpha < 1.0f) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }
    }

    /**
     * Restores render state after a previous animation pass.
     */
    public static void cleanUp(GuiGraphics gg) {
        RenderContext.endWidgetAnimation();
        gg.pose().popPose();
        gg.setColor(1.0f, 1.0f, 1.0f, RenderContext.getCurrentAlpha());
    }
}