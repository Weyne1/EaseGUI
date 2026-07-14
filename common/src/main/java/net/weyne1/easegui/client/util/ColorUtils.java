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
}