package net.weyne1.easegui.client.animator;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.weyne1.easegui.client.animation.AnimationMath;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class SplashAnimator {
    private static long lastTrackedSessionTime = -1L;
    private static long actualStartTime = -1L;

    public static void animate(
            ActiveTextCollector collector,
            TextAlignment textAlignment,
            int x,
            int y,
            ActiveTextCollector.Parameters parameters,
            Component text,
            Operation<Void> original
    ) {
        trackSessionTime();

        var screenConfig = ConfigManager.getConfig().screens.get("title");

        if (screenConfig == null || !screenConfig.enabled || screenConfig.splash == null || !screenConfig.splash.enabled) {
            original.call(collector, textAlignment, x, y, parameters, text);
            return;
        }

        var splashConfig = screenConfig.splash;
        long elapsed = Util.getMillis() - actualStartTime - splashConfig.splashDelay;

        if (elapsed <= 0) {
            return;
        }

        if (elapsed >= splashConfig.splashDuration) {
            original.call(collector, textAlignment, x, y, parameters.withOpacity(1.0f), text);
            return;
        }

        float progress = AnimationMath.calculateProgress(elapsed, splashConfig.splashDuration, splashConfig.splashEasing);

        ActiveTextCollector.Parameters animatedParams = parameters.withOpacity(AnimationMath.clamp(progress, 0f, 1f)).withScale(progress);

        original.call(collector, textAlignment, x, y, animatedParams, text);
    }

    private static void trackSessionTime() {
        long currentSessionTime = ScreenStateTracker.getScreenOpenTime();
        if (lastTrackedSessionTime != currentSessionTime) {
            lastTrackedSessionTime = currentSessionTime;
            actualStartTime = Util.getMillis();
        }
    }
}