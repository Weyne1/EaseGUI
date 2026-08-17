package net.weyne1.easegui.client.gui.preview;

import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.api.animation.CascadeDirection;
import net.weyne1.easegui.client.config.ProfileFeature;

import java.util.EnumSet;

final class PreviewLayout {
    private static final int SPACING_X = 45;
    private static final int SPACING_Y = 30;
    private final int centerX;
    private final int centerY;
    private final int boxWidth;
    private final int boxHeight;
    private final boolean cascade;
    private final boolean horizontal;
    private final int itemCount;

    private PreviewLayout(int centerX, int centerY, int boxWidth, int boxHeight, boolean cascade, boolean horizontal, int itemCount) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.cascade = cascade;
        this.horizontal = horizontal;
        this.itemCount = itemCount;
    }

    static PreviewLayout of(
            AnimationProfile profile,
            EnumSet<ProfileFeature> activeFeatures,
            int screenWidth,
            int screenHeight,
            int boxHeight
    ) {
        int centerX = (screenWidth / 2) + (screenWidth / 4);
        int centerY = screenHeight / 2;
        boolean cascade = activeFeatures.contains(ProfileFeature.CASCADE_DELAY);
        boolean horizontal = profile.getCascadeDirection() == CascadeDirection.LEFT_TO_RIGHT
                || profile.getCascadeDirection() == CascadeDirection.RIGHT_TO_LEFT;
        int boxWidth = (horizontal && cascade) ? 40 : 120;
        int itemCount = cascade ? 3 : 1;
        return new PreviewLayout(centerX, centerY, boxWidth, boxHeight, cascade, horizontal, itemCount);
    }

    int targetX(int index) {
        if (!cascade || !horizontal) return centerX;
        return centerX + (index - 1) * SPACING_X;
    }

    int targetY(int index) {
        if (!cascade || horizontal) return centerY;
        return centerY + (index - 1) * SPACING_Y;
    }

    int boxWidth() { return boxWidth; }
    int boxHeight() { return boxHeight; }
    int centerX() { return centerX; }
    int itemCount() { return itemCount; }
    boolean isCascade() { return cascade; }
    boolean isHorizontal() { return horizontal; }
}