package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.resources.ResourceLocation;
import net.weyne1.easegui.client.animation.AnimationMath;
import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.state.ScreenStateTracker;

/**
 * Animates the Minecraft title logo and edition text.
 */
public class LogoAnimator {
    private static final int LOGO_WIDTH = 256;
    private static final int LOGO_HEIGHT = 44;
    private static final int LOGO_TEXTURE_HEIGHT = 64;

    private static final int EDITION_WIDTH = 128;
    private static final int EDITION_HEIGHT = 14;
    private static final int EDITION_TEXTURE_HEIGHT = 16;

    private static final ResourceLocation[] LETTER_TEXTURES = new ResourceLocation[] {
            ResourceLocation.fromNamespaceAndPath("easegui", "textures/gui/title/letters/m.png"),
            ResourceLocation.fromNamespaceAndPath("easegui", "textures/gui/title/letters/i.png"),
            ResourceLocation.fromNamespaceAndPath("easegui", "textures/gui/title/letters/n.png"),
            ResourceLocation.fromNamespaceAndPath("easegui", "textures/gui/title/letters/e.png"),
            ResourceLocation.fromNamespaceAndPath("easegui", "textures/gui/title/letters/t.png"),
            ResourceLocation.fromNamespaceAndPath("easegui", "textures/gui/title/letters/f.png"),
            ResourceLocation.fromNamespaceAndPath("easegui", "textures/gui/title/letters/a.png"),
            ResourceLocation.fromNamespaceAndPath("easegui", "textures/gui/title/letters/r.png"),
            ResourceLocation.fromNamespaceAndPath("easegui", "textures/gui/title/letters/c.png")
    };

    /**
     * Logical letter order used for cascade animations:
     * M I N E C R A F T
     */
    private static final int[] LOGICAL_INDICES = new int[] { 0, 1, 2, 3, 8, 7, 6, 5, 4 };

    private static long lastTrackedSessionTime = -1L;
    private static long actualStartTime = -1L;

    public static boolean render(GuiGraphics gg, int screenWidth, float transparency, int height, boolean showEasterEgg, boolean keepLogoThroughFade) {
        var titleSettings = ConfigManager.getConfig().screens.get("title");
        if (titleSettings == null || !titleSettings.enabled || titleSettings.logo == null) {
            return false;
        }

        gg.flush();
        trackSessionTime();

        var logoConfig = titleSettings.logo;
        float finalAlpha = keepLogoThroughFade ? 1.0f : transparency;
        int startX = screenWidth / 2 - (LOGO_WIDTH / 2);

        ResourceLocation logoTexture = showEasterEgg ? LogoRenderer.EASTER_EGG_LOGO : LogoRenderer.MINECRAFT_LOGO;
        if (logoConfig.animateWholeText) {
            renderWholeLogo(gg, logoTexture, logoConfig.logoProfile, startX, height, finalAlpha);
        } else {
            renderCascadedLetters(gg, logoConfig.logoProfile, startX, height, finalAlpha);
        }

        renderEditionText(gg, logoConfig, screenWidth, height, finalAlpha);

        gg.flush();
        gg.setColor(1.0f, 1.0f, 1.0f, transparency);

        return true;
    }

    private static void renderWholeLogo(GuiGraphics gg, ResourceLocation texture, AnimationProfile profile, int startX, int height, float finalAlpha) {
        long elapsed = Util.getMillis() - actualStartTime;

        if (elapsed >= profile.duration) {
            drawLogoTexture(gg, texture, startX, height);
            return;
        }

        float progress = elapsed <= 0 ? 0.0f : AnimationMath.calculateProgress(elapsed, profile.duration, profile.easing);
        try (AnimationScope ignored = AnimationSystem.begin(gg, startX, height, LOGO_WIDTH, LOGO_HEIGHT, profile, progress, finalAlpha)) {
            drawLogoTexture(gg, texture, startX, height);
        }
        gg.flush();
    }

    private static void renderCascadedLetters(GuiGraphics gg, AnimationProfile profile, int startX, int height, float finalAlpha) {
        long now = Util.getMillis();
        long maxLogoDelay = (LETTER_TEXTURES.length - 1) * profile.cascadeDelay;

        if (now - actualStartTime >= maxLogoDelay + profile.duration) {
            for (ResourceLocation texture : LETTER_TEXTURES) {
                drawLogoTexture(gg, texture, startX, height);
            }
            gg.flush();
            return;
        }

        for (int i = 0; i < LETTER_TEXTURES.length; i++) {
            int logicalIndex = LOGICAL_INDICES[i];
            ResourceLocation texture = LETTER_TEXTURES[i];

            long cascadeDelay = calculateCascadeDelay(profile, logicalIndex);
            long elapsed = now - actualStartTime - cascadeDelay;
            float progress = elapsed <= 0 ? 0.0f : AnimationMath.calculateProgress(elapsed, profile.duration, profile.easing);

            try (AnimationScope ignored = AnimationSystem.begin(gg, startX, height, LOGO_WIDTH, LOGO_HEIGHT, profile, progress, finalAlpha)) {
                drawLogoTexture(gg, texture, startX, height);
            }
            gg.flush();
        }
    }

    private static void renderEditionText(GuiGraphics gg, EaseGUIConfig.LogoSettings config, int screenWidth, int height, float finalAlpha) {
        int x = screenWidth / 2 - (EDITION_WIDTH / 2);
        int y = height + LOGO_HEIGHT - 7;
        var profile = config.editionProfile;

        if (profile == null || !profile.enabled) {
            drawStaticEdition(gg, x, y, finalAlpha);
            return;
        }

        long elapsed = getEditionElapsed(config);

        if (elapsed >= profile.duration) {
            drawStaticEdition(gg, x, y, finalAlpha);
            return;
        }

        float progress = elapsed <= 0 ? 0.0f : AnimationMath.calculateProgress(elapsed, profile.duration, profile.easing);
        try (AnimationScope ignored = AnimationSystem.begin(gg, x, y, EDITION_WIDTH, EDITION_HEIGHT, profile, progress, finalAlpha)) {
            drawEditionTexture(gg, x, y);
        }
        gg.flush();
    }

    private static void drawStaticEdition(GuiGraphics gg, int x, int y, float finalAlpha) {
        try (AnimationScope ignored = AnimationSystem.beginAlphaOnly(gg, finalAlpha)) {
            drawEditionTexture(gg, x, y);
        }
        gg.flush();
    }

    private static long calculateCascadeDelay(AnimationProfile profile, int logicalIndex) {
        return switch (profile.cascadeDirection) {
            case LEFT_TO_RIGHT -> logicalIndex * profile.cascadeDelay;
            case RIGHT_TO_LEFT -> (LETTER_TEXTURES.length - 1 - logicalIndex) * profile.cascadeDelay;
            case TOP_TO_BOTTOM, BOTTOM_TO_TOP -> 0L;
        };
    }

    private static long getEditionElapsed(EaseGUIConfig.LogoSettings config) {
        long maxLogoDelay = config.animateWholeText ? 0L : switch (config.logoProfile.cascadeDirection) {
            case LEFT_TO_RIGHT, RIGHT_TO_LEFT -> (LETTER_TEXTURES.length - 1) * config.logoProfile.cascadeDelay;
            case TOP_TO_BOTTOM, BOTTOM_TO_TOP -> 0L;
        };
        return Util.getMillis() - actualStartTime - maxLogoDelay;
    }

    private static void trackSessionTime() {
        long currentSessionTime = ScreenStateTracker.getScreenOpenTime();
        if (lastTrackedSessionTime != currentSessionTime) {
            lastTrackedSessionTime = currentSessionTime;
            actualStartTime = Util.getMillis();
        }
    }

    private static void drawLogoTexture(GuiGraphics gg, ResourceLocation texture, int x, int y) {
        gg.blit(texture, x, y, 0.0f, 0.0f, LOGO_WIDTH, LOGO_HEIGHT, LOGO_WIDTH, LOGO_TEXTURE_HEIGHT);
    }

    private static void drawEditionTexture(GuiGraphics gg, int x, int y) {
        gg.blit(LogoRenderer.MINECRAFT_EDITION, x, y, 0.0f, 0.0f, EDITION_WIDTH, EDITION_HEIGHT, EDITION_WIDTH, EDITION_TEXTURE_HEIGHT);
    }
}