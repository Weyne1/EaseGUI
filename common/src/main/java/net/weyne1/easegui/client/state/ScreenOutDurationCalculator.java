package net.weyne1.easegui.client.state;

import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.api.animation.CascadeDirection;
import net.weyne1.easegui.api.animation.DirectionalAnimationProfile;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;

public class ScreenOutDurationCalculator {

    public static long calculateMaxOutDuration(Screen screen) {
        if (screen == null) return 0L;

        EaseGUIConfig config = ConfigManager.getConfig();
        if (!config.global.enabled) return 0L;

        long maxDuration = 0L;

        if (BackgroundAnimator.shouldAnimateBackground(screen)) {
            maxDuration = Math.max(maxDuration, config.global.blurDuration);
        }

        for (WidgetCategory category : WidgetCategory.values()) {
            DirectionalAnimationProfile directionalProfile = config.global.elementProfiles.get(category);
            if (directionalProfile == null) continue;

            AnimationProfile outProfile = directionalProfile.getOut();
            if (outProfile != null && outProfile.isEnabled()) {
                long profileDuration = calculateProfileOutDuration(outProfile);
                maxDuration = Math.max(maxDuration, profileDuration);
            }
        }

        return maxDuration;
    }

    private static long calculateProfileOutDuration(AnimationProfile outProfile) {
        long duration = outProfile.getDuration();

        long cascadeDelay = outProfile.getCascadeDelay();
        if (cascadeDelay > 0 && outProfile.getCascadeDirection() != null) {
            CascadeDirection cascadeDir = outProfile.getCascadeDirection();
            boolean isHorizontal = cascadeDir == CascadeDirection.LEFT_TO_RIGHT || cascadeDir == CascadeDirection.RIGHT_TO_LEFT;

            float maxDistance = isHorizontal ? 960.0f : 540.0f;
            long maxDelay = (long) (maxDistance * (cascadeDelay / 100.0f));
            duration += maxDelay;
        }

        return duration;
    }
}