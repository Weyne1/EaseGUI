package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.resources.ResourceLocation;
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

    public static boolean render(GuiGraphics gg, int screenWidth, float transparency, int height, boolean showEasterEgg, boolean keepLogoThroughFade) {
        EaseGUIConfig config = ConfigManager.getConfig();

        if (!config.global.enabled) {
            return false;
        }

        var titleSettings = config.screens.get("title");
        if (titleSettings == null || !titleSettings.enabled || titleSettings.logo == null) {
            return false;
        }

        var logoConfig = titleSettings.logo;
        float finalAlpha = 1.0f;
        int startX = screenWidth / 2 - (LogoRendererAccessor.easeGUI$getLogoWidth() / 2);

        ResourceLocation logoTexture = showEasterEgg ? LogoRenderer.EASTER_EGG_LOGO : LogoRenderer.MINECRAFT_LOGO;
        if (logoConfig.animateWholeText) {
            renderWholeLogo(gg, logoTexture, logoConfig.logoProfile, startX, height, finalAlpha);
        } else {
            renderCascadedLetters(gg, logoConfig.logoProfile, startX, height, finalAlpha);
        }

        renderEditionText(gg, logoConfig, screenWidth, height, finalAlpha);

        return true;
    }

    private static void renderWholeLogo(GuiGraphics gg, ResourceLocation texture, AnimationProfile profile, int startX, int height, float finalAlpha) {
        long actualStartTime = ScreenStateTracker.getTitleActualStartTime();
        long elapsed = Util.getMillis() - actualStartTime;
        int logoWidth = LogoRendererAccessor.easeGUI$getLogoWidth();
        int logoHeight = LogoRendererAccessor.easeGUI$getLogoHeight();

        try (AnimationScope ignored = AnimationSystem.begin(gg, startX, height, logoWidth, logoHeight, profile, elapsed, finalAlpha)) {
            drawLogoTexture(gg, texture, startX, height);
        }
    }

    private static void renderCascadedLetters(GuiGraphics gg, AnimationProfile profile, int startX, int height, float finalAlpha) {
        long now = Util.getMillis();
        long actualStartTime = ScreenStateTracker.getTitleActualStartTime();
        int logoWidth = LogoRendererAccessor.easeGUI$getLogoWidth();
        int logoHeight = LogoRendererAccessor.easeGUI$getLogoHeight();

        for (int i = 0; i < LETTER_TEXTURES.length; i++) {
            int logicalIndex = LOGICAL_INDICES[i];
            ResourceLocation texture = LETTER_TEXTURES[i];

            long cascadeDelay = calculateCascadeDelay(profile, logicalIndex);
            long elapsed = now - actualStartTime - cascadeDelay;

            try (AnimationScope ignored = AnimationSystem.begin(gg, startX, height, logoWidth, logoHeight, profile, elapsed, finalAlpha)) {
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

        if (profile == null || !profile.isEnabled()) {
            drawStaticEdition(gg, x, y, finalAlpha);
            return;
        }

        long elapsed = getEditionElapsed(config);

        if (elapsed >= profile.getDuration()) {
            drawStaticEdition(gg, x, y, finalAlpha);
            return;
        }

        try (AnimationScope ignored = AnimationSystem.begin(gg, x, y, editionWidth, editionHeight, profile, elapsed, finalAlpha)) {
            drawEditionTexture(gg, x, y);
        }
    }

    private static void drawStaticEdition(GuiGraphics gg, int x, int y, float finalAlpha) {
        try (AnimationScope ignored = AnimationSystem.beginAlphaOnly(gg, finalAlpha)) {
            drawEditionTexture(gg, x, y);
            gg.flush();
        }
    }

    private static long calculateCascadeDelay(AnimationProfile profile, int logicalIndex) {
        return switch (profile.getCascadeDirection()) {
            case LEFT_TO_RIGHT -> logicalIndex * profile.getCascadeDelay();
            case RIGHT_TO_LEFT -> (LETTER_TEXTURES.length - 1 - logicalIndex) * profile.getCascadeDelay();
            case TOP_TO_BOTTOM, BOTTOM_TO_TOP -> 0L;
        };
    }

    private static long getEditionElapsed(EaseGUIConfig.LogoSettings config) {
        long actualStartTime = ScreenStateTracker.getTitleActualStartTime();
        long maxLogoDelay = config.animateWholeText ? 0L : switch (config.logoProfile.getCascadeDirection()) {
            case LEFT_TO_RIGHT, RIGHT_TO_LEFT -> (LETTER_TEXTURES.length - 1) * config.logoProfile.getCascadeDelay();
            case TOP_TO_BOTTOM, BOTTOM_TO_TOP -> 0L;
        };
        return Util.getMillis() - actualStartTime - maxLogoDelay;
    }

    private static void drawLogoTexture(GuiGraphics gg, ResourceLocation texture, int x, int y) {
        gg.blit(texture, x, y, 0.0f, 0.0f,
                LogoRendererAccessor.easeGUI$getLogoWidth(),
                LogoRendererAccessor.easeGUI$getLogoHeight(),
                LogoRendererAccessor.easeGUI$getLogoWidth(),
                LogoRendererAccessor.easeGUI$getLogoTextureHeight());
    }

    private static void drawEditionTexture(GuiGraphics gg, int x, int y) {
        gg.blit(LogoRenderer.MINECRAFT_EDITION, x, y, 0.0f, 0.0f,
                LogoRendererAccessor.easeGUI$getEditionWidth(),
                LogoRendererAccessor.easeGUI$getEditionHeight(),
                LogoRendererAccessor.easeGUI$getEditionWidth(),
                LogoRendererAccessor.easeGUI$getEditionTextureHeight());
    }
}