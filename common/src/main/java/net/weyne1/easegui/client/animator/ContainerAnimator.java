package net.weyne1.easegui.client.animator;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.accessor.ContainerScreenAccessor;
import net.weyne1.easegui.client.accessor.RecipeBookAccessor;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationMath;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.UIElementCategory;
import net.weyne1.easegui.client.state.ScreenStateTracker;

import java.util.Map;
import java.util.WeakHashMap;

public class ContainerAnimator {
    private static final Map<Object, java.lang.ref.WeakReference<Object>> BOOK_REGISTRY = new WeakHashMap<>();

    public static void registerBook(Object screen, Object book) {
        BOOK_REGISTRY.put(screen, new java.lang.ref.WeakReference<>(book));
    }

    public static Object getBook(Object screen) {
        var ref = BOOK_REGISTRY.get(screen);
        return ref != null ? ref.get() : null;
    }

    /**
     * Evaluates container bounds (integrating the Recipe Book if open) and instantiates
     * a localized context-aware AnimationScope.
     *
     * @return a new {@link AnimationScope} mapped to the screen matrix, or {@code null} if transitions are disabled
     */
    public static AnimationScope beginScreenAnimation(Screen screen, GuiGraphics gg) {
        if (!(screen instanceof ContainerScreenAccessor container)) {
            return null;
        }

        int minX = container.easeGUI$getLeftPos();
        int minY = container.easeGUI$getTopPos();
        int maxX = minX + container.easeGUI$getImageWidth();
        int maxY = minY + container.easeGUI$getImageHeight();

        // Dynamically compute layout bounds including the Recipe Book if visible
        Object bookObj = getBook(screen);
        if (bookObj instanceof RecipeBookAccessor book && book.easeGUI$isVisible()) {
            int scrW = book.easeGUI$getScreenWidth();
            int scrH = book.easeGUI$getScreenHeight();

            int bookX = (scrW - 147) / 2 - book.easeGUI$getXOffset();
            int bookY = (scrH - 166) / 2;

            minX = Math.min(minX, bookX);
            minY = Math.min(minY, bookY);
            maxX = Math.max(maxX, bookX + 147);
            maxY = Math.max(maxY, bookY + 166);
        }

        AnimationScope scope = beginAnimation(gg, minX, minY, maxX - minX, maxY - minY);
        if (scope != null) {
            AnimationContext.pushParentAnimation();
        }
        return scope;
    }

    /**
     * Safely closes the provided animation scope and cleanly balances the global animation context depth.
     *
     * @param scope Target scope to dissolve
     */
    public static void closeScope(AnimationScope scope) {
        if (scope != null) {
            try {
                if (!scope.isClosed()) {
                    scope.close();
                }
            } finally {
                if (AnimationContext.hasParentAnimation()) {
                    AnimationContext.popParentAnimation();
                }
            }
        }
    }

    /**
     * Looks up user configurations and computes tween progress mapping to generate the core AnimationScope.
     *
     * @return an active {@link AnimationScope} or {@code null} if config conditions are unmet or duration expired
     */
    public static AnimationScope beginAnimation(GuiGraphics gg, int x, int y, int width, int height) {
        var profile = ConfigManager.getProfileForCurrentContext(UIElementCategory.CONTAINERS);
        if (profile == null || !profile.enabled) return null;

        long startTime = ScreenStateTracker.getScreenOpenTime();
        long elapsed = Util.getMillis() - startTime;

        if (elapsed >= profile.duration) return null;

        float progress = elapsed <= 0 ? 0.0f : AnimationMath.calculateProgress(elapsed, profile.duration, profile.easing);

        return AnimationSystem.begin(gg, x, y, width, height, profile, progress, 1.0f);
    }
}