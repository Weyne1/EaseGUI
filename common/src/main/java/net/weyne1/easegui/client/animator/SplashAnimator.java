package net.weyne1.easegui.client.animator;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.util.Util;
import net.weyne1.easegui.api.animation.EasingType;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class SplashAnimator {

    public static ActiveTextCollector.Parameters getAnimatedParameters(ActiveTextCollector.Parameters parameters) {
        var screenConfig = ConfigManager.getConfig().screens.get("title");

        if (screenConfig == null || !screenConfig.enabled || screenConfig.splash == null || !screenConfig.splash.enabled) {
            return parameters.withOpacity(AnimationContext.getCurrentAlpha());
        }

        var splashConfig = screenConfig.splash;
        long actualStartTime = ScreenStateTracker.getTitleActualStartTime();
        long elapsed = Util.getMillis() - actualStartTime - splashConfig.splashDelay;
        float parentAlpha = AnimationContext.getCurrentAlpha();

        if (elapsed <= 0) {
            return null;
        }

        if (elapsed >= splashConfig.splashDuration) {
            return parameters.withOpacity(parentAlpha);
        }

        float rawProgress = elapsed / (float) splashConfig.splashDuration;

        float spatialProgress = splashConfig.splashEasing != null
                ? splashConfig.splashEasing.ease(rawProgress)
                : rawProgress;

        float alphaProgress = EasingType.EASE_OUT_CUBIC.ease(rawProgress);
        float finalAlpha = Math.min(1.0f, Math.max(0.0f, alphaProgress * parentAlpha));

        return parameters
                .withOpacity(finalAlpha)
                .withScale(spatialProgress);
    }
}