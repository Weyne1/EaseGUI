package net.weyne1.easegui.client.gui.screens;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.animation.AnimationEngine;
import net.weyne1.easegui.client.animation.AnimationMathUtils;
import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.config.ProfileFeature;

import java.util.EnumSet;

public class ProfilePreviewRenderer {
    private static final int BOX_WIDTH = 120;
    private static final int BOX_HEIGHT = 24;
    private static final int HALF_W = BOX_WIDTH / 2;
    private static final int HALF_H = BOX_HEIGHT / 2;
    private static final int SPACING_Y = 29;
    private static final long LOOP_PADDING_MS = 700L;

    private static final Component STATIC_LABEL = Component.translatable("easegui.editor.preview.element");
    private static final Component[] CASCADE_LABELS = {
            Component.translatable("easegui.editor.preview.element_idx", 1),
            Component.translatable("easegui.editor.preview.element_idx", 2),
            Component.translatable("easegui.editor.preview.element_idx", 3)
    };
    private static final Component DISABLED_BADGE = Component.translatable("easegui.editor.preview.disabled");

    public static void render(GuiGraphics gg, Font font, int screenWidth, int screenHeight, AnimationProfile profile, EnumSet<ProfileFeature> activeFeatures) {
        int rightCenterX = (screenWidth / 2) + (screenWidth / 4);
        int rightCenterY = screenHeight / 2;

        boolean isCascadeActive = activeFeatures.contains(ProfileFeature.CASCADE);
        int itemCount = isCascadeActive ? 3 : 1;
        boolean isEnabled = profile.enabled;

        renderStaticBounds(gg, rightCenterX, rightCenterY, isCascadeActive, itemCount, isEnabled);
        renderAnimatedElements(gg, font, rightCenterX, rightCenterY, profile, isCascadeActive, itemCount);

        if (!isEnabled) {
            renderDisabledStatus(gg, font, rightCenterX, rightCenterY, isCascadeActive, itemCount);
        }
    }

    private static void renderStaticBounds(GuiGraphics gg, int centerX, int centerY, boolean isCascade, int count, boolean isEnabled) {
        for (int i = 0; i < count; i++) {
            int targetY = getTargetY(centerY, isCascade, i);
            int color = isEnabled ? 0xFF555555 : 0xCCAA3333;
            drawBoxOutline(gg, centerX, targetY, color);
        }
    }

    private static void renderAnimatedElements(GuiGraphics gg, Font font, int centerX, int centerY, AnimationProfile profile, boolean isCascade, int count) {
        boolean isEnabled = profile.enabled;

        long duration = Math.max(profile.duration, 50L);
        long totalLoopTime = duration + (isCascade ? (2 * profile.cascadeDelay) : 0L) + LOOP_PADDING_MS;

        long currentTime = isEnabled ? (System.currentTimeMillis() % totalLoopTime) : 0L;

        for (int i = 0; i < count; i++) {
            float easedProgress;

            if (!isEnabled) {
                easedProgress = 1.0f;
            } else {
                long itemDelay = isCascade ? calculateCascadeDelay(profile, i) : 0L;
                long itemTime = currentTime - itemDelay;
                float progress = itemTime >= duration ? 1.0f : (itemTime > 0 ? (float) itemTime / duration : 0.0f);
                easedProgress = profile.easing != null ? profile.easing.ease(progress) : progress;
            }

            int targetY = getTargetY(centerY, isCascade, i);
            int x = centerX - HALF_W;
            int y = targetY - HALF_H;

            AnimationEngine.apply(gg, x, y, BOX_WIDTH, BOX_HEIGHT, profile, easedProgress, 1.0f);

            int bgAlpha = calcAlphaColor(profile.startAlpha, easedProgress, false);
            int boxColor = isEnabled ? 0x353535 : 0x222222;
            gg.fill(x, y, x + BOX_WIDTH, y + BOX_HEIGHT, (bgAlpha << 24) | boxColor);

            Component label = isCascade ? CASCADE_LABELS[i] : STATIC_LABEL;
            int fontAlpha = calcAlphaColor(profile.startAlpha, easedProgress, true);
            int textColor = isEnabled ? 0xE0E0E0 : 0x888888;

            gg.drawCenteredString(font, label, centerX, targetY - 4, (fontAlpha << 24) | textColor);

            AnimationEngine.cleanUp(gg);
        }
    }

    private static void renderDisabledStatus(GuiGraphics gg, Font font, int centerX, int centerY, boolean isCascade, int count) {
        int lastElementY = getTargetY(centerY, isCascade, count - 1);
        int badgeY = lastElementY + HALF_H + 12;

        int textWidth = font.width(DISABLED_BADGE);
        int paddingX = 6;
        int paddingY = 3;

        gg.fill(centerX - (textWidth / 2) - paddingX, badgeY - paddingY,
                centerX + (textWidth / 2) + paddingX, badgeY + 9 + paddingY, 0x55FF5555);

        gg.drawCenteredString(font, DISABLED_BADGE, centerX, badgeY, 0xFFFF5555);
    }

    private static int getTargetY(int centerY, boolean isCascade, int index) {
        return isCascade ? centerY + (index - 1) * SPACING_Y : centerY;
    }

    private static void drawBoxOutline(GuiGraphics gg, int centerX, int targetY, int color) {
        int x1 = centerX - HALF_W - 1; int x2 = centerX + HALF_W + 1;
        int y1 = targetY - HALF_H - 1; int y2 = targetY + HALF_H + 1;

        gg.fill(x1, y1, x2, y1 + 1, color); // Top
        gg.fill(x1, y2 - 1, x2, y2, color); // Bottom
        gg.fill(x1, y1 + 1, x1 + 1, y2 - 1, color); // Left
        gg.fill(x2 - 1, y1 + 1, x2, y2 - 1, color); // Right
    }

    private static int calcAlphaColor(float startAlpha, float progress, boolean isFont) {
        float lerp = AnimationMathUtils.lerp(startAlpha, 1.0f, progress);
        float value = isFont ? AnimationMathUtils.clampFontAlpha(lerp) : lerp;
        return Math.max(0, Math.min(255, (int) (value * 255)));
    }

    private static long calculateCascadeDelay(AnimationProfile profile, int i) {
        int factor = (profile.cascadeDirection == AnimationProfile.CascadeDirection.TOP_TO_BOTTOM) ? i : (2 - i);
        return factor * profile.cascadeDelay;
    }
}