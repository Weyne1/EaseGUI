package net.weyne1.easegui.client.animator;

import net.minecraft.util.Util;
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
    /**
     * Starts the animation.
     *
     * @return an {@link AnimationScope} that must be closed, or {@code null} if no animation is needed
     */
    public static AnimationScope beginRender(GuiGraphics gg, int color) {
        var screenConfig = ConfigManager.getConfig().screens.get("title");
        if (screenConfig == null || !screenConfig.enabled || screenConfig.splash == null || !screenConfig.splash.enabled) {
            return null;
        }

        var splashConfig = screenConfig.splash;
        long startTime = ScreenStateTracker.getScreenOpenTime();
        long elapsed = Util.getMillis() - startTime - splashConfig.splashDelay;

        if (elapsed >= splashConfig.splashDuration) {
            return null;
        }

        float progress = AnimationMath.calculateProgress(elapsed, splashConfig.splashDuration, splashConfig.splashEasing);

        float baseAlpha = ((color >> 24) & 255) / 255.0f;
        float finalAlpha = baseAlpha * progress;
        AnimationScope scope = AnimationSystem.beginAlphaOnly(gg, finalAlpha);

        gg.pose().scale(progress, progress);

        return scope;
    }
}