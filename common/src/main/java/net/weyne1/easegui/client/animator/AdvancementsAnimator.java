package net.weyne1.easegui.client.animator;

import net.minecraft.util.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.api.EaseGUIScreenRegistry;
import net.weyne1.easegui.api.EaseGUIScreenType;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class AdvancementsAnimator {

    public static AnimationScope beginRenderWindow(AdvancementsScreen screen, GuiGraphics graphics) {
        EaseGUIScreenType type = EaseGUIScreenRegistry.from(screen);
        EaseGUIConfig config = ConfigManager.getConfig();

        if (!config.global.enabled) {
            return null;
        }

        var titleSettings = config.screens.get(type.getId());

        if (titleSettings == null || !titleSettings.enabled || titleSettings.advancements == null || !titleSettings.advancements.windowProfile.isEnabled()) {
            return null;
        }

        var profile = titleSettings.advancements.windowProfile;
        long startTime = ScreenStateTracker.getScreenOpenTime();
        long elapsed = Util.getMillis() - startTime;

        if (elapsed >= profile.getDuration()) return null;

        return AnimationSystem.begin(graphics, profile, 0, 0, screen.width, screen.height, elapsed, 1.0f);
    }

    public static AnimationScope beginRenderTab(Screen screen, GuiGraphics graphics, int tabIndex) {
        EaseGUIScreenType type = EaseGUIScreenRegistry.from(screen);
        EaseGUIConfig config = ConfigManager.getConfig();

        if (!config.global.enabled) {
            return null;
        }

        var titleSettings = config.screens.get(type.getId());

        if (titleSettings == null || !titleSettings.enabled || titleSettings.advancements == null || !titleSettings.advancements.tabsProfile.isEnabled()) {
            return null;
        }

        float parentAlpha = AnimationContext.getCurrentAlpha();

        if (tabIndex == 0) {
            return AnimationSystem.beginAlphaOnly(graphics, parentAlpha);
        }

        var profile = titleSettings.advancements.tabsProfile;
        long startTime = ScreenStateTracker.getScreenOpenTime();
        long tabDelay = tabIndex * profile.getCascadeDelay();

        long elapsed = Util.getMillis() - startTime - tabDelay;

        if (elapsed >= profile.getDuration()) return null;

        return AnimationSystem.begin(graphics, profile, 0, 0, 28, 32, elapsed, parentAlpha);
    }
}