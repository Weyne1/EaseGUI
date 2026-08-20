package net.weyne1.easegui.client.animator;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.extension.ContainerScreenExtension;
import net.weyne1.easegui.client.extension.RecipeBookComponentExtension;
import net.weyne1.easegui.client.extension.RecipeBookScreenExtension;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class ContainerAnimator {

    public static AnimationScope beginContainer(Screen screen, GuiGraphicsExtractor graphics) {
        if (!(screen instanceof ContainerScreenExtension container)) {
            return AnimationScope.NO_OP;
        }

        int minX = container.easegui$getLeftPos();
        int minY = container.easegui$getTopPos();
        int width = container.easegui$getImageWidth();
        int height = container.easegui$getImageHeight();

        int maxX = minX + width;
        int maxY = minY + height;

        if (screen instanceof RecipeBookScreenExtension recipeScreen) {
            RecipeBookComponent<?> book = recipeScreen.easegui$getRecipeBookComponent();
            if (book != null && book.isVisible()) {
                RecipeBookComponentExtension accessor = (RecipeBookComponentExtension) book;
                minX = Math.min(minX, accessor.easegui$getXOrigin());
                minY = Math.min(minY, accessor.easegui$getYOrigin());
                maxX = Math.max(maxX, accessor.easegui$getXOrigin() + RecipeBookComponent.IMAGE_WIDTH);
                maxY = Math.max(maxY, accessor.easegui$getYOrigin() + RecipeBookComponent.IMAGE_HEIGHT);
            }
        }

        AnimationProfile profile = ConfigManager.getProfileForCurrentContext(WidgetCategory.CONTAINERS);
        long startTime = ScreenStateTracker.getScreenOpenTime();
        return AnimationSystem.begin(graphics, profile, minX, minY, maxX - minX, maxY - minY, startTime, 0L, 1.0f);
    }
}