package net.weyne1.easegui.client.animator;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.weyne1.easegui.client.extension.ContainerScreenExtension;
import net.weyne1.easegui.client.extension.RecipeBookComponentExtension;
import net.weyne1.easegui.client.extension.RecipeBookScreenExtension;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class ContainerAnimator {

    public static AnimationScope beginAnimation(Screen screen, GuiGraphicsExtractor graphics) {
        if (!(screen instanceof ContainerScreenExtension container)) {
            return null;
        }

        int minX = container.easeGUI$getLeftPos();
        int minY = container.easeGUI$getTopPos();
        int maxX = minX + container.easeGUI$getImageWidth();
        int maxY = minY + container.easeGUI$getImageHeight();

        if (screen instanceof RecipeBookScreenExtension recipeScreen) {
            RecipeBookComponent<?> book = recipeScreen.easeGUI$getRecipeBookComponent();
            if (book != null && ((RecipeBookComponentExtension) book).easeGUI$isVisible()) {
                RecipeBookComponentExtension accessor = (RecipeBookComponentExtension) book;
                minX = Math.min(minX, accessor.easeGUI$getXOrigin());
                minY = Math.min(minY, accessor.easeGUI$getYOrigin());
                maxX = Math.max(maxX, accessor.easeGUI$getXOrigin() + RecipeBookComponent.IMAGE_WIDTH);
                maxY = Math.max(maxY, accessor.easeGUI$getYOrigin() + RecipeBookComponent.IMAGE_HEIGHT);
            }
        }

        var profile = ConfigManager.getProfileForCurrentContext(WidgetCategory.CONTAINERS);
        if (profile == null || !profile.isEnabled()) return null;

        long startTime = ScreenStateTracker.getScreenOpenTime();
        return AnimationSystem.begin(graphics, minX, minY, maxX - minX, maxY - minY, profile, startTime, 0L, 1.0f);
    }
}