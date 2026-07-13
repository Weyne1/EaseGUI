package net.weyne1.easegui.client.animator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIElementCategory;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class ListItemAnimator {

    public static AnimationScope beginRender(GuiGraphics gg, int top, int left, int width, int height) {
        var profile = ConfigManager.getProfileForCurrentContext(EaseGUIElementCategory.LIST_ENTRY);
        if (profile == null || !profile.enabled) return null;

        long delay = getDelay(top, left, profile);
        long startTime = ScreenStateTracker.getScreenOpenTime();

        return AnimationSystem.begin(gg, left, top, width, height, profile, startTime, delay, 1.0f);
    }

    private static long getDelay(int top, int left, AnimationProfile profile) {
        var window = Minecraft.getInstance().getWindow();
        int screenHeight = window.getGuiScaledHeight();
        int screenWidth = window.getGuiScaledWidth();

        float distance = switch (profile.cascadeDirection) {
            case TOP_TO_BOTTOM -> top;
            case BOTTOM_TO_TOP -> Math.max(0f, screenHeight - top);
            case LEFT_TO_RIGHT -> left;
            case RIGHT_TO_LEFT -> Math.max(0f, screenWidth - left);
        };

        return (long) (distance * (profile.cascadeDelay / 100.0f));
    }
}