package net.weyne1.easegui.client.state;

import net.minecraft.util.Mth;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.config.ConfigManager;

public class ScreenAnimationTracker {

    public static float getProgress() {
        var globalConfig = ConfigManager.getConfig().global;

        if (!globalConfig.enableSmoothDimming || BackgroundAnimator.skipBackgroundFade) {
            return 1.0f;
        }

        long elapsed = ScreenStateTracker.getScreenElapsed();
        long duration = globalConfig.dimmingDuration;

        float t = (duration <= 0) ? 1f : Mth.clamp(elapsed / (float) duration, 0f, 1f);

        return globalConfig.dimmingEasing.ease(t);
    }
}