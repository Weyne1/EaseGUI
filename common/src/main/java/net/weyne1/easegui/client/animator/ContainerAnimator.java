package net.weyne1.easegui.client.animator;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.extension.ContainerScreenExtension;
import net.weyne1.easegui.client.extension.RecipeBookExtension;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class ContainerAnimator {

    public static AnimationScope beginContainer(Screen screen, GuiGraphics graphics) {
        if (!(screen instanceof ContainerScreenExtension container)) {
            return AnimationScope.NO_OP;
        }

        int minX = container.easegui$getLeftPos();
        int minY = container.easegui$getTopPos();
        int width = container.easegui$getImageWidth();
        int height = container.easegui$getImageHeight();

        int maxX = minX + width;
        int maxY = minY + height;

        if (screen instanceof RecipeUpdateListener listener) {
            RecipeBookComponent book = listener.getRecipeBookComponent();

            if (((RecipeBookExtension) book).easegui$isVisible()) {
                RecipeBookExtension accessor = (RecipeBookExtension) book;

                int bookWidth = accessor.easegui$getBookWidth();
                int bookHeight = accessor.easegui$getBookHeight();

                int bookX = (screen.width - bookWidth) / 2 - accessor.easegui$getXOffset();
                int bookY = (screen.height - bookHeight) / 2;

                minX = Math.min(minX, bookX);
                minY = Math.min(minY, bookY);
                maxX = Math.max(maxX, bookX + bookWidth);
                maxY = Math.max(maxY, bookY + bookHeight);
            }
        }

        AnimationProfile profile = ConfigManager.getProfileForCurrentContext(WidgetCategory.CONTAINERS);
        long startTime = ScreenStateTracker.getScreenOpenTime();
        return AnimationSystem.begin(graphics, profile, minX, minY, maxX - minX, maxY - minY, startTime, 0L, 1.0f);
    }
}