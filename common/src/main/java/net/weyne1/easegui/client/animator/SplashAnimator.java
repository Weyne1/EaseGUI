package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.animation.AnimationEngine;
import net.weyne1.easegui.client.animation.AnimationMathUtils;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.state.ScreenStateTracker;

/**
 * Animates the title screen splash text.
 */
public class SplashAnimator {

    public static boolean shouldAnimate() {
        var titleSettings = ConfigManager.getConfig().screens.get("title");
        return titleSettings != null && titleSettings.enabled && titleSettings.splash != null && titleSettings.splash.enabled;
    }

    public static void preRenderLocal(GuiGraphics gg, int color) {
        var splashConfig = ConfigManager.getConfig().screens.get("title").splash;
        long startTime = ScreenStateTracker.getScreenOpenTime();

        long elapsed = Util.getMillis() - startTime - splashConfig.splashDelay;
        float progress = AnimationMathUtils.calculateProgress(elapsed, splashConfig.splashDuration, splashConfig.splashEasing);

        float baseAlpha = ((color >> 24) & 255) / 255.0f;
        float finalAlpha = baseAlpha * AnimationMathUtils.clampFontAlpha(progress);

        AnimationEngine.applyAlphaOnly(gg, finalAlpha);
        gg.pose().pushPose();

        float localYOffset = (1.0f - progress) * -10.0f;
        gg.pose().translate(0.0f, localYOffset, 0.0f);

        float introScale = 0.8f + (progress * 0.2f);
        gg.pose().scale(introScale, introScale, 1.0f);
    }

    public static void postRenderLocal(GuiGraphics gg) {
        gg.pose().popPose();
        AnimationEngine.cleanUp(gg);
    }
}