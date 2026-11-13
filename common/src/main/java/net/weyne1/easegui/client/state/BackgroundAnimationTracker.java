package net.weyne1.easegui.client.state;

import net.minecraft.util.Mth;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.config.ConfigManager;

public class BackgroundAnimationTracker {

    public static float getProgress() {
        var globalConfig = ConfigManager.getConfig().global;
        long duration = globalConfig.backgroundAnimationDuration;

        if (duration <= 0 || BackgroundAnimator.isBackgroundAnimationSkipped()) {
            return 1.0f;
        }

        long elapsed = ScreenStateTracker.getScreenElapsed();
        float t = Mth.clamp(elapsed / (float) duration, 0f, 1f);

        return globalConfig.backgroundAnimationEasing.ease(t);
    }
}