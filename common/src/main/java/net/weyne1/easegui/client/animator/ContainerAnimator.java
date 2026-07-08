package net.weyne1.easegui.client.animator;

import net.minecraft.util.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.weyne1.easegui.client.accessor.ContainerScreenAccessor;
import net.weyne1.easegui.client.accessor.RecipeBookComponentAccessor;
import net.weyne1.easegui.client.accessor.RecipeBookScreenAccessor;
import net.weyne1.easegui.client.animation.AnimationMath;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.UIElementCategory;
import net.weyne1.easegui.client.state.ScreenStateTracker;

public class ContainerAnimator {
    /**
     * Starts the animation.
     *
     * @return an {@link AnimationScope} that must be closed, or {@code null} if no animation is needed
     */
    public static AnimationScope beginAnimation(Screen screen, GuiGraphics gg) {
        if (!(screen instanceof ContainerScreenAccessor container)) {
            return null;
        }

        int minX = container.easeGUI$getLeftPos();
        int minY = container.easeGUI$getTopPos();
        int maxX = minX + container.easeGUI$getImageWidth();
        int maxY = minY + container.easeGUI$getImageHeight();

        if (screen instanceof RecipeBookScreenAccessor recipeScreen) {
            RecipeBookComponent<?> book = recipeScreen.easeGUI$getRecipeBookComponent();
            if (book != null && ((RecipeBookComponentAccessor) book).easeGUI$isVisible()) {
                RecipeBookComponentAccessor accessor = (RecipeBookComponentAccessor) book;
                minX = Math.min(minX, accessor.easeGUI$getXOrigin());
                minY = Math.min(minY, accessor.easeGUI$getYOrigin());
                maxX = Math.max(maxX, accessor.easeGUI$getXOrigin() + RecipeBookComponent.IMAGE_WIDTH);
                maxY = Math.max(maxY, accessor.easeGUI$getYOrigin() + RecipeBookComponent.IMAGE_HEIGHT);
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