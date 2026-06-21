package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.resources.ResourceLocation;
import net.weyne1.easegui.client.animation.AnimationEngine;
import net.weyne1.easegui.client.animation.AnimationMathUtils;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.ModConfig;
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

    public static boolean render(GuiGraphics gg, int screenWidth, float transparency, int height,
                                 boolean showEasterEgg, boolean keepLogoThroughFade) {

        var titleSettings = ConfigManager.getConfig().screens.get("title");
        if (titleSettings == null || !titleSettings.enabled || titleSettings.logo == null) {
            return false;
        }

        var logoConfig = titleSettings.logo;
        long startTime = ScreenStateTracker.getScreenOpenTime();
        float finalAlpha = keepLogoThroughFade ? 1.0f : transparency;

        ResourceLocation logoTexture = showEasterEgg ? LogoRenderer.EASTER_EGG_LOGO : LogoRenderer.MINECRAFT_LOGO;
        int startX = screenWidth / 2 - (LOGO_WIDTH / 2);

        renderLogoLetters(gg, logoTexture, logoConfig, startX, height, startTime, finalAlpha);

        int totalElements = logoConfig.animateWholeText ? 1 : LETTER_TEXTURES.length;
        renderEditionText(gg, logoConfig, totalElements, screenWidth, height, startTime, finalAlpha);

        return true;
    }

    private static void renderLogoLetters(GuiGraphics gg, ResourceLocation vanillaTexture, ModConfig.LogoSettings config,
                                          int startX, int height, long startTime, float finalAlpha) {
        var profile = config.logoProfile;
        long now = Util.getMillis();

        if (config.animateWholeText) {
            long elapsed = now - startTime;
            float progress = AnimationMathUtils.calculateProgress(elapsed, profile.duration, profile.easing);

            AnimationEngine.apply(gg, startX, height, LOGO_WIDTH, LOGO_HEIGHT, profile, progress, finalAlpha);
            gg.blit(vanillaTexture, startX, height, 0.0f, 0.0f, LOGO_WIDTH, LOGO_HEIGHT, LOGO_WIDTH, LOGO_TEXTURE_HEIGHT);
            AnimationEngine.cleanUp(gg);
        } else {
            for (int i = 0; i < LETTER_TEXTURES.length; i++) {
                int logicalIndex = LOGICAL_INDICES[i];
                ResourceLocation texture = LETTER_TEXTURES[i];

                long cascadeDelay = (config.direction == ModConfig.LogoSettings.Direction.LEFT_TO_RIGHT)
                        ? logicalIndex * profile.cascadeDelay
                        : (LETTER_TEXTURES.length - 1 - logicalIndex) * profile.cascadeDelay;

                long elapsed = now - startTime - cascadeDelay;
                float progress = AnimationMathUtils.calculateProgress(elapsed, profile.duration, profile.easing);

                AnimationEngine.apply(gg, startX, height, LOGO_WIDTH, LOGO_HEIGHT, profile, progress, finalAlpha);
                gg.blit(texture, startX, height, 0.0f, 0.0f, LOGO_WIDTH, LOGO_HEIGHT, LOGO_WIDTH, LOGO_TEXTURE_HEIGHT);
                AnimationEngine.cleanUp(gg);
            }
        }
    }

    private static void renderEditionText(GuiGraphics gg, ModConfig.LogoSettings config, int elementsCount,
                                          int screenWidth, int height, long startTime, float finalAlpha) {
        int x = screenWidth / 2 - (EDITION_WIDTH / 2);
        int y = height + LOGO_HEIGHT - 7;

        var profile = config.editionProfile;

        if (profile != null && profile.enabled) {
            long now = Util.getMillis();
            long editionDelay = elementsCount * config.logoProfile.cascadeDelay;

            long elapsed = now - startTime - editionDelay;
            float progress = AnimationMathUtils.calculateProgress(elapsed, profile.duration, profile.easing);

            AnimationEngine.apply(gg, x, y, EDITION_WIDTH, EDITION_HEIGHT, profile, progress, finalAlpha);
        } else {
            AnimationEngine.applyAlphaOnly(gg, finalAlpha);
        }

        gg.blit(LogoRenderer.MINECRAFT_EDITION, x, y, 0.0f, 0.0f, EDITION_WIDTH, EDITION_HEIGHT, EDITION_WIDTH, EDITION_TEXTURE_HEIGHT);
        AnimationEngine.cleanUp(gg);
    }
}