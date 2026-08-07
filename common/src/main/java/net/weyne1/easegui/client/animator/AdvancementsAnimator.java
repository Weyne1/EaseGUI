package net.weyne1.easegui.client.animator;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.weyne1.easegui.api.EaseGUIScreenRegistry;
import net.weyne1.easegui.api.EaseGUIScreenType;
import net.weyne1.easegui.api.animation.AnimationDirection;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class AdvancementsAnimator {
    public static AnimationScope beginRenderWindow(AdvancementsScreen screen, GuiGraphicsExtractor graphics) {
        EaseGUIScreenType type = EaseGUIScreenRegistry.from(screen);
        EaseGUIConfig config = ConfigManager.getConfig();

        if (!config.global.enabled) {
            return null;
        }

        var titleSettings = config.screens.get(type.getId());

        if (titleSettings == null || !titleSettings.enabled || titleSettings.advancements == null) {
            return null;
        }

        AnimationDirection direction = ScreenStateTracker.isClosing() ? AnimationDirection.OUT : AnimationDirection.IN;
        AnimationProfile profile = titleSettings.advancements.windowProfile != null ? titleSettings.advancements.windowProfile.getForDirection(direction) : null;

        if (profile == null || !profile.isEnabled()) {
            return null;
        }

        long startTime = ScreenStateTracker.isClosing() ? ScreenStateTracker.getClosingStartTime() : ScreenStateTracker.getScreenOpenTime();

        return AnimationSystem.begin(
                graphics,
                profile,
                direction,
                0,
                0,
                screen.width,
                screen.height,
                startTime,
                0L,
                1.0f
        );
    }

    public static AnimationScope beginRenderTab(Screen screen, GuiGraphicsExtractor graphics, int tabIndex) {
        EaseGUIScreenType type = EaseGUIScreenRegistry.from(screen);
        EaseGUIConfig config = ConfigManager.getConfig();

        if (!config.global.enabled) {
            return null;
        }

        var titleSettings = config.screens.get(type.getId());
        if (titleSettings == null || !titleSettings.enabled || titleSettings.advancements == null) {
            return null;
        }

        AnimationDirection direction = ScreenStateTracker.isClosing() ? AnimationDirection.OUT : AnimationDirection.IN;
        AnimationProfile profile = titleSettings.advancements.tabsProfile != null ? titleSettings.advancements.tabsProfile.getForDirection(direction) : null;

        if (profile == null || !profile.isEnabled()) {
            return null;
        }

        float parentAlpha = AnimationContext.getCurrentAlpha();

        if (tabIndex == 0) {
            return AnimationSystem.beginAlphaOnly(graphics, parentAlpha);
        }

        long startTime = ScreenStateTracker.isClosing() ? ScreenStateTracker.getClosingStartTime() : ScreenStateTracker.getScreenOpenTime();
        long tabDelay = tabIndex * profile.getCascadeDelay();

        return AnimationSystem.begin(
                graphics,
                profile,
                direction,
                0,
                0,
                28,
                32,
                startTime,
                tabDelay,
                parentAlpha
        );
    }
}