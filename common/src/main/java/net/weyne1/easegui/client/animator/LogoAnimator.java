package net.weyne1.easegui.client.animator;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Util;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.resources.Identifier;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.mixin.accessor.LogoRendererAccessor;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import org.jetbrains.annotations.Nullable;

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

    // Logical letter order used for cascade animations: M I N E C R A F T
    private static final int[] LOGICAL_INDICES = new int[] { 0, 1, 2, 3, 8, 7, 6, 5, 4 };

    public static boolean render(GuiGraphicsExtractor graphics, int screenWidth, float transparency, int height, boolean showEasterEgg, boolean keepLogoThroughFade) {
        EaseGUIConfig.LogoSettings logoConfig = getLogoConfig();
        if (logoConfig == null) {
            return false;
        }

        float finalAlpha = keepLogoThroughFade ? 1.0f : transparency;
        int startX = screenWidth / 2 - (LogoRendererAccessor.easeGUI$getLogoWidth() / 2);

        Identifier logoTexture = showEasterEgg ? LogoRenderer.EASTER_EGG_LOGO : LogoRenderer.MINECRAFT_LOGO;
        if (logoConfig.animateWholeText) {
            renderWholeLogo(graphics, logoTexture, logoConfig.logoProfile, startX, height, finalAlpha);
        } else {
            renderCascadedLetters(graphics, logoConfig.logoProfile, startX, height, finalAlpha);
        }

        renderEditionText(graphics, logoConfig, screenWidth, height, finalAlpha);

        return true;
    }

    private static void renderWholeLogo(GuiGraphicsExtractor graphics, Identifier texture, AnimationProfile profile, int startX, int height, float finalAlpha) {
        long actualStartTime = ScreenStateTracker.getTitleActualStartTime();
        long elapsed = Util.getMillis() - actualStartTime;
        int logoWidth = LogoRendererAccessor.easeGUI$getLogoWidth();
        int logoHeight = LogoRendererAccessor.easeGUI$getLogoHeight();

        try (AnimationScope ignored = AnimationSystem.begin(graphics, profile, startX, height, logoWidth, logoHeight, elapsed, finalAlpha)) {
            drawLogoTexture(graphics, texture, startX, height);
        }
    }

    private static void renderCascadedLetters(GuiGraphicsExtractor graphics, AnimationProfile profile, int startX, int height, float finalAlpha) {
        long now = Util.getMillis();
        long actualStartTime = ScreenStateTracker.getTitleActualStartTime();
        int logoWidth = LogoRendererAccessor.easeGUI$getLogoWidth();
        int logoHeight = LogoRendererAccessor.easeGUI$getLogoHeight();

        for (int i = 0; i < LETTER_TEXTURES.length; i++) {
            int logicalIndex = LOGICAL_INDICES[i];
            Identifier texture = LETTER_TEXTURES[i];

            long cascadeDelay = calculateCascadeDelay(profile, logicalIndex);
            long elapsed = now - actualStartTime - cascadeDelay;

            try (AnimationScope ignored = AnimationSystem.begin(graphics, profile, startX, height, logoWidth, logoHeight, elapsed, finalAlpha)) {
                drawLogoTexture(graphics, texture, startX, height);
            }
        }
    }

    private static void renderEditionText(GuiGraphicsExtractor graphics, EaseGUIConfig.LogoSettings config, int screenWidth, int height, float finalAlpha) {
        int editionWidth = LogoRendererAccessor.easeGUI$getEditionWidth();
        int editionHeight = LogoRendererAccessor.easeGUI$getEditionHeight();
        int logoHeight = LogoRendererAccessor.easeGUI$getLogoHeight();
        int x = screenWidth / 2 - (editionWidth / 2);
        int y = height + logoHeight - 7;

        long elapsed = getEditionElapsed(config);

        try (AnimationScope ignored = (config.editionProfile != null)
                ? AnimationSystem.begin(graphics, config.editionProfile, x, y, editionWidth, editionHeight, elapsed, finalAlpha)
                : AnimationSystem.beginAlphaOnly(graphics, finalAlpha)) {

            drawEditionTexture(graphics, x, y);
        }
    }

    private static long calculateCascadeDelay(@Nullable AnimationProfile profile, int logicalIndex) {
        if (profile == null) return 0L;

        return switch (profile.getCascadeDirection()) {
            case LEFT_TO_RIGHT -> logicalIndex * profile.getCascadeDelay();
            case RIGHT_TO_LEFT -> (LETTER_TEXTURES.length - 1 - logicalIndex) * profile.getCascadeDelay();
            case TOP_TO_BOTTOM, BOTTOM_TO_TOP -> 0L;
        };
    }

    private static long getEditionElapsed(EaseGUIConfig.LogoSettings config) {
        long actualStartTime = ScreenStateTracker.getTitleActualStartTime();

        long maxLogoDelay = 0L;
        if (!config.animateWholeText && config.logoProfile != null) {
            maxLogoDelay = switch (config.logoProfile.getCascadeDirection()) {
                case LEFT_TO_RIGHT, RIGHT_TO_LEFT -> (LETTER_TEXTURES.length - 1) * config.logoProfile.getCascadeDelay();
                case TOP_TO_BOTTOM, BOTTOM_TO_TOP -> 0L;
            };
        }

        return Util.getMillis() - actualStartTime - maxLogoDelay;
    }

    @Nullable
    private static EaseGUIConfig.LogoSettings getLogoConfig() {
        EaseGUIConfig config = ConfigManager.getConfig();
        if (!config.global.enabled) return null;

        var titleSettings = config.screens.get("title");
        if (titleSettings == null || !titleSettings.enabled || titleSettings.logo == null) {
            return null;
        }
        return titleSettings.logo;
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