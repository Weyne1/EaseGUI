package net.weyne1.easegui.client.util;

import net.weyne1.easegui.client.animation.AnimationContext;

public final class ColorUtils {

    private ColorUtils() {}

    public static int getAnimatedColor(int originalColor) {
        int originalAlpha = (originalColor >> 24) & 0xFF;

        if (originalAlpha == 0 && (originalColor & 0x00FFFFFF) != 0) {
            originalAlpha = 255;
        }

        float animationAlpha = AnimationContext.getCurrentAlpha();
        int finalAlpha = Math.round(originalAlpha * animationAlpha);

        return (originalColor & 0x00FFFFFF) | (finalAlpha << 24);
    }

    public static int multiplyPremultiplied(int color, float factor) {
        if (factor >= 1.0f) return color;
        if (factor <= 0.0f) return 0;

        int a = (int) (((color >> 24) & 0xFF) * factor);
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}