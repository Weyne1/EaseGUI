package net.weyne1.easegui.client.state;

import net.minecraft.Util;
import net.weyne1.easegui.client.config.ConfigManager;

public class ScreenAnimationTracker {
    private static long blurStartTime = -1;
    private static long lastRenderTime = 0;

    public static float getProgress() {
        var globalConfig = ConfigManager.getConfig().global;
        if (!globalConfig.enableSmoothBlur) {
            return 1.0f;
        }

        long now = Util.getMillis();

        if (now - lastRenderTime > 200) {
            blurStartTime = now;
        }
        lastRenderTime = now;

        long elapsed = now - blurStartTime;
        long duration = globalConfig.blurDuration;

        float t;
        if (elapsed <= 0) t = 0f;
        else if (elapsed >= duration) t = 1f;
        else t = elapsed / (float) duration;

        return globalConfig.blurEasing.ease(t);
    }
}