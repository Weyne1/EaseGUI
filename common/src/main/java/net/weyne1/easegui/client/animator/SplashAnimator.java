package net.weyne1.easegui.client.animator;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.util.Util;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationMath;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class SplashAnimator {
    private static long lastTrackedSessionTime = -1L;
    private static long actualStartTime = -1L;

    public static ActiveTextCollector.Parameters getAnimatedParameters(ActiveTextCollector.Parameters parameters) {
        trackSessionTime();

        var screenConfig = ConfigManager.getConfig().screens.get("title");

        if (screenConfig == null || !screenConfig.enabled || screenConfig.splash == null || !screenConfig.splash.enabled) {
            return parameters.withOpacity(AnimationContext.getCurrentAlpha());
        }

        var splashConfig = screenConfig.splash;
        long elapsed = Util.getMillis() - actualStartTime - splashConfig.splashDelay;
        float parentAlpha = AnimationContext.getCurrentAlpha();

        if (elapsed <= 0) {
            return null;
        }

        if (elapsed >= splashConfig.splashDuration) {
            return parameters.withOpacity(parentAlpha);
        }

        float progress = AnimationMath.calculateProgress(elapsed, splashConfig.splashDuration, splashConfig.splashEasing);
        float finalAlpha = AnimationMath.clamp(progress * parentAlpha, 0f, 1f);

        return parameters
                .withOpacity(finalAlpha)
                .withScale(progress);
    }

    private static void trackSessionTime() {
        long currentSessionTime = ScreenStateTracker.getScreenOpenTime();
        if (lastTrackedSessionTime != currentSessionTime) {
            lastTrackedSessionTime = currentSessionTime;
            actualStartTime = Util.getMillis();
        }
    }
}