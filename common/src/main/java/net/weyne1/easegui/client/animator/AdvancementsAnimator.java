package net.weyne1.easegui.client.animator;

import net.minecraft.util.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationMath;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.api.EaseGUIScreenRegistry;
import net.weyne1.easegui.api.EaseGUIScreenType;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class AdvancementsAnimator {

    public static AnimationScope beginRenderWindow(AdvancementsScreen screen, GuiGraphics gg) {
        EaseGUIScreenType type = EaseGUIScreenRegistry.from(screen);
        var titleSettings = ConfigManager.getConfig().screens.get(type.getId());

        if (titleSettings == null || !titleSettings.enabled || titleSettings.advancements == null || !titleSettings.advancements.windowProfile.isEnabled()) {
            return null;
        }

        var profile = titleSettings.advancements.windowProfile;
        long startTime = ScreenStateTracker.getScreenOpenTime();
        long elapsed = Util.getMillis() - startTime;

        if (elapsed >= profile.getDuration()) return null;

        float progress = elapsed <= 0 ? 0.0f : AnimationMath.calculateProgress(elapsed, profile.getDuration(), profile.getEasing());

        return AnimationSystem.begin(gg, 0, 0, screen.width, screen.height, profile, progress, 1.0f);
    }

    public static AnimationScope beginRenderTab(Screen screen, GuiGraphics gg, int tabIndex) {
        EaseGUIScreenType type = EaseGUIScreenRegistry.from(screen);
        var titleSettings = ConfigManager.getConfig().screens.get(type.getId());

        if (titleSettings == null || !titleSettings.enabled || titleSettings.advancements == null || !titleSettings.advancements.tabsProfile.isEnabled()) {
            return null;
        }

        float parentAlpha = AnimationContext.getCurrentAlpha();

        if (tabIndex == 0) {
            return AnimationSystem.beginAlphaOnly(gg, parentAlpha);
        }

        var profile = titleSettings.advancements.tabsProfile;
        long startTime = ScreenStateTracker.getScreenOpenTime();
        long tabDelay = tabIndex * profile.getCascadeDelay();

        long elapsed = Util.getMillis() - startTime - tabDelay;

        if (elapsed >= profile.getDuration()) return null;

        float progress = 0.0f;
        if (elapsed > 0) {
            progress = AnimationMath.calculateProgress(elapsed, profile.getDuration(), profile.getEasing());
        }

        return AnimationSystem.begin(gg, 0, 0, 28, 32, profile, progress, parentAlpha);
    }
}