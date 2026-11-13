package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.animator.ContainerAnimator;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.extension.ContainerScreenExtension;
import net.weyne1.easegui.client.state.BackgroundAnimationTracker;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import net.weyne1.easegui.client.util.Blur;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Screen.class)
@SuppressWarnings({"ConstantConditions", "NameDoesntMatchTargetClass"})
public abstract class ScreenMixin {

    @Shadow @Nullable protected Minecraft minecraft;

    // Container lifecycle

    @WrapMethod(method = "renderWithTooltip")
    private void easegui$wrapScreenRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        Screen currentScreen = (Screen) (Object) this;
        ScreenStateTracker.markScreenRendered(currentScreen);

        // Container lifecycle
        if (currentScreen instanceof ContainerScreenExtension
                && !ScreenStateTracker.shouldSkipContainerAnimation()
                && !AnimationContext.isAnimationDisabled()) {
            try (AnimationScope ignored = ContainerAnimator.beginContainer(currentScreen, graphics)) {
                AnimationContext.pushParentAnimation();
                try {
                    original.call(graphics, mouseX, mouseY, partialTick);
                } finally {
                    AnimationContext.popParentAnimation();
                }
            }
        } else {
            original.call(graphics, mouseX, mouseY, partialTick);
        }
    }

    // Menu background

    @WrapMethod(method = "renderBackground")
    private void easegui$wrapBackground(GuiGraphics graphics, Operation<Void> original) {
        AnimationScope currentScope = AnimationContext.getCurrentScope();
        if (currentScope != null) currentScope.suspend();

        try {
            Screen currentScreen = (Screen) (Object) this;
            boolean blurAllTransparentScreens = ConfigManager.getConfig().global.blurAllTransparentScreens;
            boolean smoothBlurEnabled = ConfigManager.getConfig().global.backgroundAnimationDuration > 0;

            boolean isRealScreenFrame = this.minecraft != null && this.minecraft.level != null
                    && this.minecraft.screen == (Object) this;

            if (isRealScreenFrame && blurAllTransparentScreens && smoothBlurEnabled) {
                try {
                    graphics.flush();
                    float progress = BackgroundAnimator.isBackgroundEffectAllowed(currentScreen) ? BackgroundAnimationTracker.getProgress() : 1.0f;
                    float partialTick = this.minecraft.getFrameTime();

                    Blur.renderBlur(progress, partialTick);
                    ScreenStateTracker.markScreenBlurred((Screen) (Object) this);
                } catch (Exception ignored) {
                }
            }

            try (AnimationScope ignored = BackgroundAnimator.beginRenderMenu(currentScreen, graphics)) {
                original.call(graphics);
            }
        } finally {
            if (currentScope != null) currentScope.resume();
        }
    }

    // Gradient color

    @ModifyArg(
            method = "renderBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"),
            index = 4
    )
    private int easegui$modifyTransparentBgStartColor(int startColor) {
        Screen currentScreen = (Screen) (Object) this;

        return BackgroundAnimator.getAnimatedColor(currentScreen, easegui$applyDimmingIntensity(startColor));
    }

    @ModifyArg(
            method = "renderBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"),
            index = 5
    )
    private int easegui$modifyTransparentBgEndColor(int endColor) {
        Screen currentScreen = (Screen) (Object) this;

        return BackgroundAnimator.getAnimatedColor(currentScreen, easegui$applyDimmingIntensity(endColor));
    }

    @Unique
    private static int easegui$applyDimmingIntensity(int color) {
        float intensity = ConfigManager.getConfig().global.backgroundDimmingIntensity;

        int targetAlpha = Math.min(255, Math.max(0, Math.round(intensity * 255.0f)));
        return (targetAlpha << 24) | (color & 0x00FFFFFF);
    }
}