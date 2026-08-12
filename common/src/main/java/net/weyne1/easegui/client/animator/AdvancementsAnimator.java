package net.weyne1.easegui.client.animator;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class AdvancementsAnimator {
    public static AnimationScope beginWindow(AdvancementsScreen screen, GuiGraphicsExtractor graphics) {
        AnimationProfile profile = ConfigManager.getProfile(screen, settings -> settings.windowProfile);
        long startTime = ScreenStateTracker.getScreenOpenTime();

        return AnimationSystem.begin(graphics, profile, 0, 0, screen.width, screen.height, startTime, 0, 1.0f);
    }

    public static AnimationScope beginTab(Screen screen, GuiGraphicsExtractor graphics, int tabIndex) {
        float parentAlpha = AnimationContext.getCurrentAlpha();

        if (tabIndex == 0) {
            return AnimationSystem.beginAlphaOnly(graphics, parentAlpha);
        }

        AnimationProfile profile = ConfigManager.getProfile(screen, settings -> settings.tabsProfile);
        long startTime = ScreenStateTracker.getScreenOpenTime();
        long cascadeDelay = (profile != null) ? tabIndex * profile.getCascadeDelay() : 0;

        return AnimationSystem.begin(graphics, profile, 0, 0, 28, 32, startTime, cascadeDelay, parentAlpha);
    }
}