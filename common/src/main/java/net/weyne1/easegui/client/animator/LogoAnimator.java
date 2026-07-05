package net.weyne1.easegui.client.animator;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.resources.Identifier;
import net.weyne1.easegui.client.animation.AnimationMath;
import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.mixin.accessor.LogoRendererAccessor;
import net.weyne1.easegui.client.state.ScreenStateTracker;

/**
 * Animates the Minecraft title logo and edition text.
 */
public class LogoAnimator {

    private static final Identifier[] LETTER_TEXTURES = new Identifier[] {
            Identifier.fromNamespaceAndPath("easegui", "textures/gui/title/letters/m.png"),
            Identifier.fromNamespaceAndPath("easegui", "textures/gui/title/letters/i.png"),
            Identifier.fromNamespaceAndPath("easegui", "textures/gui/title/letters/n.png"),
            Identifier.fromNamespaceAndPath("easegui", "textures/gui/title/letters/e.png"),
            Identifier.fromNamespaceAndPath("easegui", "textures/gui/title/letters/t.png"),
            Identifier.fromNamespaceAndPath("easegui", "textures/gui/title/letters/f.png"),
            Identifier.fromNamespaceAndPath("easegui", "textures/gui/title/letters/a.png"),
            Identifier.fromNamespaceAndPath("easegui", "textures/gui/title/letters/r.png"),
            Identifier.fromNamespaceAndPath("easegui", "textures/gui/title/letters/c.png")
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

        trackSessionTime();

        var logoConfig = titleSettings.logo;
        float finalAlpha = keepLogoThroughFade ? 1.0f : transparency;
        int startX = screenWidth / 2 - (LogoRendererAccessor.easeGUI$getLogoWidth() / 2);

        Identifier logoTexture = showEasterEgg ? LogoRenderer.EASTER_EGG_LOGO : LogoRenderer.MINECRAFT_LOGO;
        if (logoConfig.animateWholeText) {
            renderWholeLogo(gg, logoTexture, logoConfig.logoProfile, startX, height, finalAlpha);
        } else {
            renderCascadedLetters(gg, logoConfig.logoProfile, startX, height, finalAlpha);
        }

        renderEditionText(gg, logoConfig, screenWidth, height, finalAlpha);

        return true;
    }

    private static void renderWholeLogo(GuiGraphics gg, Identifier texture, AnimationProfile profile, int startX, int height, float finalAlpha) {
        long elapsed = Util.getMillis() - actualStartTime;
        int logoWidth = LogoRendererAccessor.easeGUI$getLogoWidth();
        int logoHeight = LogoRendererAccessor.easeGUI$getLogoHeight();

        if (elapsed >= profile.duration) {
            try (AnimationScope ignored = AnimationSystem.beginAlphaOnly(gg, finalAlpha)) {
                drawLogoTexture(gg, texture, startX, height);
            }
            return;
        }

        float progress = elapsed <= 0 ? 0.0f : AnimationMath.calculateProgress(elapsed, profile.duration, profile.easing);
        try (AnimationScope ignored = AnimationSystem.begin(gg, startX, height, logoWidth, logoHeight, profile, progress, finalAlpha)) {
            drawLogoTexture(gg, texture, startX, height);
        }
    }

    private static void renderCascadedLetters(GuiGraphics gg, AnimationProfile profile, int startX, int height, float finalAlpha) {
        long now = Util.getMillis();
        long maxLogoDelay = (LETTER_TEXTURES.length - 1) * profile.cascadeDelay;
        int logoWidth = LogoRendererAccessor.easeGUI$getLogoWidth();
        int logoHeight = LogoRendererAccessor.easeGUI$getLogoHeight();

        if (now - actualStartTime >= maxLogoDelay + profile.duration) {
            try (AnimationScope ignored = AnimationSystem.beginAlphaOnly(gg, finalAlpha)) {
                for (Identifier texture : LETTER_TEXTURES) {
                    drawLogoTexture(gg, texture, startX, height);
                }
            }
            return;
        }

        for (int i = 0; i < LETTER_TEXTURES.length; i++) {
            int logicalIndex = LOGICAL_INDICES[i];
            Identifier texture = LETTER_TEXTURES[i];

            long cascadeDelay = calculateCascadeDelay(profile, logicalIndex);
            long elapsed = now - actualStartTime - cascadeDelay;
            float progress = elapsed <= 0 ? 0.0f : AnimationMath.calculateProgress(elapsed, profile.duration, profile.easing);

            try (AnimationScope ignored = AnimationSystem.begin(gg, startX, height, logoWidth, logoHeight, profile, progress, finalAlpha)) {
                drawLogoTexture(gg, texture, startX, height);
            }
        }
    }

    private static void renderEditionText(GuiGraphics gg, EaseGUIConfig.LogoSettings config, int screenWidth, int height, float finalAlpha) {
        int editionWidth = LogoRendererAccessor.easeGUI$getEditionWidth();
        int editionHeight = LogoRendererAccessor.easeGUI$getEditionHeight();
        int logoHeight = LogoRendererAccessor.easeGUI$getLogoHeight();
        int x = screenWidth / 2 - (editionWidth / 2);
        int y = height + logoHeight - 7;
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
        try (AnimationScope ignored = AnimationSystem.begin(gg, x, y, editionWidth, editionHeight, profile, progress, finalAlpha)) {
            drawEditionTexture(gg, x, y);
        }
    }

    private static void drawStaticEdition(GuiGraphics gg, int x, int y, float finalAlpha) {
        try (AnimationScope ignored = AnimationSystem.beginAlphaOnly(gg, finalAlpha)) {
            drawEditionTexture(gg, x, y);
        }
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

    private static void drawLogoTexture(GuiGraphics gg, Identifier texture, int x, int y) {
        gg.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0f, 0.0f,
                LogoRendererAccessor.easeGUI$getLogoWidth(),
                LogoRendererAccessor.easeGUI$getLogoHeight(),
                LogoRendererAccessor.easeGUI$getLogoWidth(),
                LogoRendererAccessor.easeGUI$getLogoTextureHeight());
    }

    private static void drawEditionTexture(GuiGraphics gg, int x, int y) {
        gg.blit(RenderPipelines.GUI_TEXTURED, LogoRenderer.MINECRAFT_EDITION, x, y, 0.0f, 0.0f,
                LogoRendererAccessor.easeGUI$getEditionWidth(),
                LogoRendererAccessor.easeGUI$getEditionHeight(),
                LogoRendererAccessor.easeGUI$getEditionWidth(),
                LogoRendererAccessor.easeGUI$getEditionTextureHeight());
    }
}