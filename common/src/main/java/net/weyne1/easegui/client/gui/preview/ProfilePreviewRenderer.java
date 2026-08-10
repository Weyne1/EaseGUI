package net.weyne1.easegui.client.gui.preview;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.api.animation.CascadeDirection;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ProfileFeature;

import java.util.EnumSet;

public class ProfilePreviewRenderer {
    private static final int BOX_HEIGHT = 24;
    private static final int SPACING_Y = 29;
    private static final int SPACING_X = 45;
    private static final long LOOP_PADDING_MS = 700L;

    private static final Component STATIC_LABEL = Component.translatable("easegui.editor.preview.element");

    private static final Component[] CASCADE_LABELS = {
            Component.translatable("easegui.editor.preview.element_idx", 1),
            Component.translatable("easegui.editor.preview.element_idx", 2),
            Component.translatable("easegui.editor.preview.element_idx", 3)
    };

    private static final Component[] CASCADE_SHORT_LABELS = {
            Component.literal("#1"),
            Component.literal("#2"),
            Component.literal("#3")
    };

    private static final Component DISABLED_BADGE = Component.translatable("easegui.editor.preview.disabled");

    public static void render(GuiGraphics graphics, Font font, int screenWidth, int screenHeight, AnimationProfile profile, EnumSet<ProfileFeature> activeFeatures) {
        int rightCenterX = (screenWidth / 2) + (screenWidth / 4);
        int rightCenterY = screenHeight / 2;

        boolean isCascadeActive = activeFeatures.contains(ProfileFeature.CASCADE_DELAY);
        int itemCount = isCascadeActive ? 3 : 1;
        boolean isEnabled = profile.isEnabled();

        boolean isHorizontal = profile.getCascadeDirection() == CascadeDirection.LEFT_TO_RIGHT ||
                profile.getCascadeDirection() == CascadeDirection.RIGHT_TO_LEFT;

        int boxWidth = (isHorizontal && isCascadeActive) ? 40 : 120;

        renderStaticBounds(graphics, rightCenterX, rightCenterY, isCascadeActive, itemCount, isEnabled, isHorizontal, boxWidth);
        renderAnimatedElements(graphics, font, rightCenterX, rightCenterY, profile, isCascadeActive, itemCount, isHorizontal, boxWidth);

        if (!isEnabled) {
            renderDisabledStatus(graphics, font, rightCenterX, rightCenterY, isCascadeActive, itemCount, isHorizontal);
        }
    }

    private static void renderStaticBounds(GuiGraphics graphics, int centerX, int centerY, boolean isCascade, int count, boolean isEnabled, boolean isHorizontal, int boxWidth) {
        int color = isEnabled ? 0xFF555555 : 0xCCAA3333;
        for (int i = 0; i < count; i++) {
            int targetX = getTargetX(centerX, isCascade, isHorizontal, i);
            int targetY = getTargetY(centerY, isCascade, isHorizontal, i);
            drawBoxOutline(graphics, targetX, targetY, boxWidth, color);
        }
    }

    private static void renderAnimatedElements(GuiGraphics graphics, Font font, int centerX, int centerY, AnimationProfile profile, boolean isCascade, int count, boolean isHorizontal, int boxWidth) {
        boolean isEnabled = profile.isEnabled();
        long duration = Math.max(profile.getDuration(), 50L);
        long totalLoopTime = duration + (isCascade ? (2 * profile.getCascadeDelay()) : 0L) + LOOP_PADDING_MS;
        long currentTime = isEnabled ? (System.currentTimeMillis() % totalLoopTime) : 0L;

        int halfW = boxWidth / 2;
        int halfH = BOX_HEIGHT / 2;

        for (int i = 0; i < count; i++) {
            float rawProgress;

            if (!isEnabled) {
                rawProgress = 1.0f;
            } else {
                long itemDelay = isCascade ? calculateCascadeDelay(profile, i) : 0L;
                long itemTime = currentTime - itemDelay;
                rawProgress = itemTime >= duration ? 1.0f : (itemTime > 0 ? (float) itemTime / duration : 0.0f);
            }

            int targetX = getTargetX(centerX, isCascade, isHorizontal, i);
            int targetY = getTargetY(centerY, isCascade, isHorizontal, i);

            int x = targetX - halfW;
            int y = targetY - halfH;

            try (AnimationScope scope = AnimationSystem.begin(graphics, profile, x, y, boxWidth, BOX_HEIGHT, rawProgress, 1.0f)) {
                float currentAlpha = scope.getAlpha();
                int bgAlpha = (int) (currentAlpha * 255.0f);

                int boxColor = isEnabled ? 0x353535 : 0x222222;
                graphics.fill(x, y, x + boxWidth, y + BOX_HEIGHT, (bgAlpha << 24) | boxColor);

                Component label = isCascade
                        ? (isHorizontal ? CASCADE_SHORT_LABELS[i] : CASCADE_LABELS[i])
                        : STATIC_LABEL;

                int textColor = isEnabled ? 0xE0E0E0 : 0x888888;
                graphics.drawCenteredString(font, label, targetX, targetY - 4, (bgAlpha << 24) | textColor);
            }
        }
    }

    private static void renderDisabledStatus(GuiGraphics graphics, Font font, int centerX, int centerY, boolean isCascade, int count, boolean isHorizontal) {
        int lastElementY = getTargetY(centerY, isCascade, isHorizontal, count - 1);
        int badgeY = lastElementY + (BOX_HEIGHT / 2) + 12;

        int textWidth = font.width(DISABLED_BADGE);
        int paddingX = 6;
        int paddingY = 3;

        graphics.fill(centerX - (textWidth / 2) - paddingX, badgeY - paddingY,
                centerX + (textWidth / 2) + paddingX, badgeY + 9 + paddingY, 0x55FF5555);
        graphics.drawCenteredString(font, DISABLED_BADGE, centerX, badgeY, 0xFFFF5555);
    }

    private static int getTargetX(int centerX, boolean isCascade, boolean isHorizontal, int index) {
        if (!isCascade || !isHorizontal) return centerX;
        return centerX + (index - 1) * SPACING_X;
    }

    private static int getTargetY(int centerY, boolean isCascade, boolean isHorizontal, int index) {
        if (!isCascade || isHorizontal) return centerY;
        return centerY + (index - 1) * SPACING_Y;
    }

    private static void drawBoxOutline(GuiGraphics graphics, int centerX, int targetY, int boxWidth, int color) {
        int halfW = boxWidth / 2;
        int halfH = BOX_HEIGHT / 2;

        int x1 = centerX - halfW - 1; int x2 = centerX + halfW + 1;
        int y1 = targetY - halfH - 1; int y2 = targetY + halfH + 1;

        int dashLength = 4;
        int gapLength = 2;
        int step = dashLength + gapLength;

        // Upper face
        for (int x = x1; x < x2; x += step) {
            int endX = Math.min(x + dashLength, x2);
            graphics.fill(x, y1, endX, y1 + 1, color);
        }

        // Bottom face
        for (int x = x1; x < x2; x += step) {
            int endX = Math.min(x + dashLength, x2);
            graphics.fill(x, y2 - 1, endX, y2, color);
        }

        // Left face
        for (int y = y1 + 1; y < y2 - 1; y += step) {
            int endY = Math.min(y + dashLength, y2 - 1);
            graphics.fill(x1, y, x1 + 1, endY, color);
        }

        // Right face
        for (int y = y1 + 1; y < y2 - 1; y += step) {
            int endY = Math.min(y + dashLength, y2 - 1);
            graphics.fill(x2 - 1, y, x2, endY, color);
        }
    }

    private static long calculateCascadeDelay(AnimationProfile profile, int i) {
        boolean reverse = profile.getCascadeDirection() == CascadeDirection.BOTTOM_TO_TOP ||
                profile.getCascadeDirection() == CascadeDirection.RIGHT_TO_LEFT;

        int factor = reverse ? (2 - i) : i;
        return factor * profile.getCascadeDelay();
    }
}