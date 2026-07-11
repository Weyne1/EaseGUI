package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.weyne1.easegui.client.accessor.ContainerScreenAccessor;
import net.weyne1.easegui.client.accessor.RecipeBookAccessor;
import net.weyne1.easegui.client.animation.AnimationMath;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.UIElementCategory;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class ContainerAnimator {

    public static AnimationScope beginAnimation(Screen screen, GuiGraphics gg) {
        if (!(screen instanceof ContainerScreenAccessor container)) {
            return null;
        }

        int minX = container.easeGUI$getLeftPos();
        int minY = container.easeGUI$getTopPos();
        int maxX = minX + container.easeGUI$getImageWidth();
        int maxY = minY + container.easeGUI$getImageHeight();

        if (screen instanceof RecipeUpdateListener listener) {
            RecipeBookComponent book = listener.getRecipeBookComponent();

            if (((RecipeBookAccessor) book).easeGUI$isVisible()) {
                RecipeBookAccessor accessor = (RecipeBookAccessor) book;

                int bookWidth = accessor.easeGUI$getBookWidth();
                int bookHeight = accessor.easeGUI$getBookHeight();

                int bookX = (screen.width - bookWidth) / 2 - accessor.easeGUI$getXOffset();
                int bookY = (screen.height - bookHeight) / 2;

                minX = Math.min(minX, bookX);
                minY = Math.min(minY, bookY);
                maxX = Math.max(maxX, bookX + bookWidth);
                maxY = Math.max(maxY, bookY + bookHeight);
            }
        }

        var profile = ConfigManager.getProfileForCurrentContext(UIElementCategory.CONTAINERS);
        if (profile == null || !profile.enabled) return null;

        long startTime = ScreenStateTracker.getScreenOpenTime();
        long elapsed = Util.getMillis() - startTime;
        if (elapsed >= profile.duration) return null;

        float progress = elapsed <= 0 ? 0.0f : AnimationMath.calculateProgress(elapsed, profile.duration, profile.easing);

        return AnimationSystem.begin(gg, minX, minY, maxX - minX, maxY - minY, profile, progress, 1.0f);
    }
}