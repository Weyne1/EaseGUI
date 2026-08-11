package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationMath;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class SplashAnimator {

    public static AnimationScope beginRender(GuiGraphics graphics, int x, int y, int color) {
        EaseGUIConfig config = ConfigManager.getConfig();

        if (!config.global.enabled) {
            return null;
        }

        var screenConfig = config.screens.get("title");
        if (screenConfig == null || !screenConfig.enabled || screenConfig.splash == null || !screenConfig.splash.enabled) {
            return null;
        }

        var splashConfig = screenConfig.splash;
        long actualStartTime = ScreenStateTracker.getTitleActualStartTime();
        long elapsed = Util.getMillis() - actualStartTime - splashConfig.splashDelay;

        if (elapsed <= 0) {
            return AnimationSystem.beginAlphaOnly(graphics, 0.0f);
        }

        if (elapsed >= splashConfig.splashDuration) {
            return null;
        }

        float progress = AnimationMath.calculateProgress(elapsed, splashConfig.splashDuration, splashConfig.splashEasing);

        float parentAlpha = AnimationContext.getCurrentAlpha();
        float baseAlpha = ((color >> 24) & 255) / 255.0f;
        if (baseAlpha == 0.0f) {
            baseAlpha = 1.0f;
        }

        float finalAlpha = AnimationMath.clamp(baseAlpha * progress * parentAlpha, 0.0f, 1.0f);

        return AnimationSystem.beginPivotScale(graphics, x, y, progress, finalAlpha);
    }
}