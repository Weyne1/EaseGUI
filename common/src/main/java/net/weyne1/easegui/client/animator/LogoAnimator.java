package net.weyne1.easegui.client.animator;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Util;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.resources.Identifier;
import net.weyne1.easegui.api.animation.AnimationDirection;
import net.weyne1.easegui.api.animation.AnimationProfile;
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

    public static boolean render(GuiGraphicsExtractor graphics, int screenWidth, float transparency, int height, boolean showEasterEgg, boolean keepLogoThroughFade) {
        EaseGUIConfig config = ConfigManager.getConfig();

        if (!config.global.enabled) {
            return false;
        }

        var titleSettings = config.screens.get("title");
        if (titleSettings == null || !titleSettings.enabled || titleSettings.logo == null) {
            return false;
        }

        var logoConfig = titleSettings.logo;
        float finalAlpha = keepLogoThroughFade ? 1.0f : transparency;
        int startX = screenWidth / 2 - (LogoRendererAccessor.easeGUI$getLogoWidth() / 2);
        Identifier logoTexture = showEasterEgg ? LogoRenderer.EASTER_EGG_LOGO : LogoRenderer.MINECRAFT_LOGO;

        AnimationDirection direction = ScreenStateTracker.isClosing() ? AnimationDirection.OUT : AnimationDirection.IN;
        AnimationProfile logoProfile = logoConfig.logoProfile != null ? logoConfig.logoProfile.getForDirection(direction) : null;

        if (logoProfile == null || !logoProfile.isEnabled()) {
            drawStaticLogo(graphics, logoTexture, startX, height, finalAlpha);
        } else if (logoConfig.animateWholeText) {
            renderWholeLogo(graphics, logoTexture, logoProfile, direction, startX, height, finalAlpha);
        } else {
            renderCascadedLetters(graphics, logoProfile, direction, startX, height, finalAlpha);
        }

        renderEditionText(graphics, logoConfig, logoProfile, direction, screenWidth, height, finalAlpha);

        return true;
    }

    private static void renderWholeLogo(GuiGraphicsExtractor graphics, Identifier texture, AnimationProfile profile,
                                        AnimationDirection direction, int startX, int height, float finalAlpha) {
        long actualStartTime = ScreenStateTracker.getTitleActualStartTime();
        long elapsed = Util.getMillis() - actualStartTime;
        int logoWidth = LogoRendererAccessor.easeGUI$getLogoWidth();
        int logoHeight = LogoRendererAccessor.easeGUI$getLogoHeight();

        try (AnimationScope ignored = AnimationSystem.begin(
                graphics, profile, direction, startX, height, logoWidth, logoHeight, elapsed, finalAlpha
        )) {
            drawLogoTexture(graphics, texture, startX, height);
        }
    }

    private static void renderCascadedLetters(GuiGraphicsExtractor graphics, AnimationProfile profile,
                                              AnimationDirection direction, int startX, int height, float finalAlpha) {
        long now = Util.getMillis();
        long actualStartTime = ScreenStateTracker.getTitleActualStartTime();
        int logoWidth = LogoRendererAccessor.easeGUI$getLogoWidth();
        int logoHeight = LogoRendererAccessor.easeGUI$getLogoHeight();

        for (int i = 0; i < LETTER_TEXTURES.length; i++) {
            int logicalIndex = LOGICAL_INDICES[i];
            Identifier texture = LETTER_TEXTURES[i];

            long cascadeDelay = calculateCascadeDelay(profile, logicalIndex);
            long elapsed = now - actualStartTime - cascadeDelay;

            try (AnimationScope ignored = AnimationSystem.begin(
                    graphics, profile, direction, startX, height, logoWidth, logoHeight, elapsed, finalAlpha
            )) {
                drawLogoTexture(graphics, texture, startX, height);
            }
        }
    }

    private static void renderEditionText(GuiGraphicsExtractor graphics, EaseGUIConfig.LogoSettings config, AnimationProfile logoProfile,
                                          AnimationDirection direction, int screenWidth, int height, float finalAlpha) {
        int editionWidth = LogoRendererAccessor.easeGUI$getEditionWidth();
        int editionHeight = LogoRendererAccessor.easeGUI$getEditionHeight();
        int logoHeight = LogoRendererAccessor.easeGUI$getLogoHeight();
        int x = screenWidth / 2 - (editionWidth / 2);
        int y = height + logoHeight - 7;

        AnimationProfile profile = config.editionProfile != null ? config.editionProfile.getForDirection(direction) : null;

        if (profile == null || !profile.isEnabled()) {
            drawStaticEdition(graphics, x, y, finalAlpha);
            return;
        }

        long elapsed = getEditionElapsed(config, logoProfile);

        if (elapsed >= profile.getDuration()) {
            drawStaticEdition(graphics, x, y, finalAlpha);
            return;
        }

        try (AnimationScope ignored = AnimationSystem.begin(
                graphics, profile, direction, x, y, editionWidth, editionHeight, elapsed, finalAlpha
        )) {
            drawEditionTexture(graphics, x, y);
        }
    }

    private static void drawStaticLogo(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, float finalAlpha) {
        try (AnimationScope ignored = AnimationSystem.beginAlphaOnly(graphics, finalAlpha)) {
            drawLogoTexture(graphics, texture, x, y);
        }
    }

    private static void drawStaticEdition(GuiGraphicsExtractor graphics, int x, int y, float finalAlpha) {
        try (AnimationScope ignored = AnimationSystem.beginAlphaOnly(graphics, finalAlpha)) {
            drawEditionTexture(graphics, x, y);
        }
    }

    private static long calculateCascadeDelay(AnimationProfile profile, int logicalIndex) {
        return switch (profile.getCascadeDirection()) {
            case LEFT_TO_RIGHT -> logicalIndex * profile.getCascadeDelay();
            case RIGHT_TO_LEFT -> (LETTER_TEXTURES.length - 1 - logicalIndex) * profile.getCascadeDelay();
            case TOP_TO_BOTTOM, BOTTOM_TO_TOP -> 0L;
        };
    }

    private static long getEditionElapsed(EaseGUIConfig.LogoSettings config, AnimationProfile logoProfile) {
        long actualStartTime = ScreenStateTracker.getTitleActualStartTime();

        if (config.animateWholeText || logoProfile == null) {
            return Util.getMillis() - actualStartTime;
        }

        long maxLogoDelay = switch (logoProfile.getCascadeDirection()) {
            case LEFT_TO_RIGHT, RIGHT_TO_LEFT -> (LETTER_TEXTURES.length - 1) * logoProfile.getCascadeDelay();
            case TOP_TO_BOTTOM, BOTTOM_TO_TOP -> 0L;
        };

        return Util.getMillis() - actualStartTime - maxLogoDelay;
    }

    private static void drawLogoTexture(GuiGraphicsExtractor graphics, Identifier texture, int x, int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0f, 0.0f,
                LogoRendererAccessor.easeGUI$getLogoWidth(),
                LogoRendererAccessor.easeGUI$getLogoHeight(),
                LogoRendererAccessor.easeGUI$getLogoWidth(),
                LogoRendererAccessor.easeGUI$getLogoTextureHeight());
    }

    private static void drawEditionTexture(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, LogoRenderer.MINECRAFT_EDITION, x, y, 0.0f, 0.0f,
                LogoRendererAccessor.easeGUI$getEditionWidth(),
                LogoRendererAccessor.easeGUI$getEditionHeight(),
                LogoRendererAccessor.easeGUI$getEditionWidth(),
                LogoRendererAccessor.easeGUI$getEditionTextureHeight());
    }
}