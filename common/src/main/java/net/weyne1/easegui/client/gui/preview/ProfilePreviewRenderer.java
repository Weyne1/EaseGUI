package net.weyne1.easegui.client.gui.preview;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.api.animation.CascadeDirection;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.ProfileFeature;

import java.util.EnumSet;

public class ProfilePreviewRenderer {
    private static final int BOX_HEIGHT = 24;
    private static final long LOOP_PADDING_MS = 700L;
    private static final Component DISABLED_BADGE = Component.translatable("easegui.editor.preview.disabled");
    private static final Component DISABLED_GLOBALLY_BADGE = Component.translatable("easegui.editor.preview.disabled_globally");

    public static void render(
            GuiGraphics graphics,
            Font font,
            AnimationProfile profile,
            EnumSet<ProfileFeature> activeFeatures,
            int screenWidth,
            int screenHeight
    ) {
        PreviewLayout layout = PreviewLayout.of(profile, activeFeatures, screenWidth, screenHeight, BOX_HEIGHT);
        boolean isEnabled = profile.isEnabled() && ConfigManager.getConfig().global.enabled;

        PreviewContentRenderer contentRenderer = new AbstractElementPreview(layout.isCascade(), layout.isHorizontal());

        renderStaticBounds(graphics, layout, isEnabled);
        renderAnimatedElements(graphics, font, profile, layout, contentRenderer, isEnabled);

        if (!isEnabled) {
            renderDisabledStatus(graphics, font, layout);
        }
    }

    private static void renderStaticBounds(
            GuiGraphics graphics,
            PreviewLayout layout,
            boolean isEnabled
    ) {
        int color = isEnabled ? 0xFF555555 : 0xCCAA3333;
        for (int i = 0; i < layout.itemCount(); i++) {
            drawBoxOutline(graphics, layout.targetX(i), layout.targetY(i), layout.boxWidth(), layout.boxHeight(), color);
        }
    }

    private static void renderAnimatedElements(
            GuiGraphics graphics,
            Font font,
            AnimationProfile profile,
            PreviewLayout layout,
            PreviewContentRenderer contentRenderer,
            boolean isEnabled
    ) {
        long duration = Math.max(profile.getDuration(), 50L);
        long totalLoopTime = duration + (layout.isCascade() ? (2 * profile.getCascadeDelay()) : 0L) + LOOP_PADDING_MS;
        long currentTime = isEnabled ? (System.currentTimeMillis() % totalLoopTime) : 0L;

        for (int i = 0; i < layout.itemCount(); i++) {
            long itemDelay = layout.isCascade() ? calculateCascadeDelay(profile, i) : 0L;
            long itemElapsed = currentTime - itemDelay;

            int targetX = layout.targetX(i);
            int targetY = layout.targetY(i);
            int x = targetX - layout.boxWidth() / 2;
            int y = targetY - layout.boxHeight() / 2;

            try (AnimationScope scope = AnimationSystem.begin(graphics, profile, x, y, layout.boxWidth(), layout.boxHeight(), itemElapsed, 1.0f)) {
                int alpha = (int) (scope.getAlpha() * 255.0f);
                contentRenderer.render(graphics, font, x, y, layout.boxWidth(), layout.boxHeight(), i, alpha, isEnabled);
            }
        }
    }

    private static void renderDisabledStatus(
            GuiGraphics graphics,
            Font font,
            PreviewLayout layout
    ) {
        int lastElementY = layout.targetY(layout.itemCount() - 1);
        int badgeY = lastElementY + (layout.boxHeight() / 2) + 12;

        Component disabledBadge = ConfigManager.getConfig().global.enabled ? DISABLED_BADGE : DISABLED_GLOBALLY_BADGE;

        int textWidth = font.width(disabledBadge);
        int paddingX = 6;
        int paddingY = 3;

        int centerX = layout.centerX();

        graphics.fill(centerX - (textWidth / 2) - paddingX, badgeY - paddingY,
                centerX + (textWidth / 2) + paddingX, badgeY + 9 + paddingY, 0x55FF5555);
        graphics.drawCenteredString(font, disabledBadge, centerX, badgeY, 0xFFFF5555);
    }

    private static void drawBoxOutline(
            GuiGraphics graphics,
            int centerX,
            int targetY,
            int boxWidth,
            int boxHeight,
            int color
    ) {
        int halfW = boxWidth / 2;
        int halfH = boxHeight / 2;

        int x1 = centerX - halfW - 1;
        int x2 = centerX + halfW + 1;
        int y1 = targetY - halfH - 1;
        int y2 = targetY + halfH + 1;

        int dashLength = 4;
        int gapLength = 2;
        int step = dashLength + gapLength;

        for (int x = x1; x < x2; x += step) { // Upper face
            int endX = Math.min(x + dashLength, x2);
            graphics.fill(x, y1, endX, y1 + 1, color);
        }

        for (int x = x1; x < x2; x += step) { // Bottom face
            int endX = Math.min(x + dashLength, x2);
            graphics.fill(x, y2 - 1, endX, y2, color);
        }

        for (int y = y1 + 1; y < y2 - 1; y += step) { // Left face
            int endY = Math.min(y + dashLength, y2 - 1);
            graphics.fill(x1, y, x1 + 1, endY, color);
        }

        for (int y = y1 + 1; y < y2 - 1; y += step) { // Right face
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