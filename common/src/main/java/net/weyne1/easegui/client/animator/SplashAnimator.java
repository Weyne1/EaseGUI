package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.animation.AnimationMath;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
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

    /**
     * Starts the animation.
     *
     * @return an {@link AnimationScope} that must be closed, or {@code null} if no animation is needed
     */
    public static AnimationScope beginRender(GuiGraphics gg, int color) {
        if (!shouldAnimate()) return null;

        var splashConfig = ConfigManager.getConfig().screens.get("title").splash;
        long startTime = ScreenStateTracker.getScreenOpenTime();

        long elapsed = Util.getMillis() - startTime - splashConfig.splashDelay;

        if (elapsed >= splashConfig.splashDuration) {
            return null;
        }

        float progress = AnimationMath.calculateProgress(elapsed, splashConfig.splashDuration, splashConfig.splashEasing);

        float baseAlpha = ((color >> 24) & 255) / 255.0f;
        float finalAlpha = baseAlpha * AnimationMath.clampFontAlpha(progress);

        AnimationScope scope = AnimationSystem.beginAlphaOnly(gg, finalAlpha);

        float localYOffset = (1.0f - progress) * -10.0f;
        gg.pose().translate(0.0f, localYOffset, 0.0f);

        float introScale = 0.8f + (progress * 0.2f);
        gg.pose().scale(introScale, introScale, 1.0f);

        return scope;
    }
}