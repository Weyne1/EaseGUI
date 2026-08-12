package net.weyne1.easegui.client.animator;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.util.Util;
import net.weyne1.easegui.api.animation.EasingType;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationMath;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import org.jetbrains.annotations.Nullable;

public class SplashAnimator {

    @Nullable
    public static ActiveTextCollector.Parameters getAnimatedParameters(ActiveTextCollector.Parameters parameters) {
        float parentAlpha = AnimationContext.getCurrentAlpha();

        if (!isSplashEnabled()) {
            return parameters.withOpacity(parentAlpha);
        }

        var splashConfig = ConfigManager.getConfig().screens.get("title").splash;
        long actualStartTime = ScreenStateTracker.getTitleActualStartTime();
        long elapsed = Util.getMillis() - actualStartTime - splashConfig.splashDelay;

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
        float finalAlpha = AnimationMath.clamp(alphaProgress * parentAlpha, 0.0f, 1.0f);

        return parameters.withOpacity(finalAlpha).withScale(spatialProgress);
    }

    private static boolean isSplashEnabled() {
        EaseGUIConfig config = ConfigManager.getConfig();
        if (!config.global.enabled) return false;

        var screenConfig = config.screens.get("title");
        return screenConfig != null
                && screenConfig.enabled
                && screenConfig.splash != null
                && screenConfig.splash.enabled;
    }
}