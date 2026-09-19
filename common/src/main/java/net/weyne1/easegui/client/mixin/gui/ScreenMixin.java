package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.animator.ContainerAnimator;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.extension.ContainerScreenExtension;
import net.weyne1.easegui.client.state.ScreenAnimationTracker;
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
    private void easeGUI$wrapScreenRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        if (RenderSystem.isOnRenderThread()
                && this instanceof ContainerScreenExtension
                && !AnimationContext.isAnimationDisabled()) {
            try (AnimationScope ignored = ContainerAnimator.beginAnimation((Screen) (Object) this, graphics)) {
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
    private void easeGUI$wrapBackground(GuiGraphics graphics, Operation<Void> original) {
        AnimationScope currentScope = AnimationContext.getCurrentScope();
        if (currentScope != null) currentScope.suspend();

        try {
            Screen currentScreen = (Screen) (Object) this;
            boolean blurBackground = ConfigManager.getConfig().global.blurContainers;
            boolean enableSmoothBlur = ConfigManager.getConfig().global.enableSmoothBlur;

            boolean isRealScreenFrame = this.minecraft != null && this.minecraft.level != null
                    && this.minecraft.screen == (Object) this;

            if (isRealScreenFrame && blurBackground) {
                try {
                    graphics.flush();
                    float progress = enableSmoothBlur && BackgroundAnimator.shouldAnimateBackground(currentScreen) ? ScreenAnimationTracker.getProgress() : 1.0f;
                    float partialTick = this.minecraft.getFrameTime();

                    Blur.renderBlur(progress, partialTick);
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
    private int easeGUI$modifyTransparentBgStartColor(int startColor) {
        Screen currentScreen = (Screen) (Object) this;
        if (!BackgroundAnimator.shouldAnimateBackground(currentScreen)) {
            return startColor;
        }
        return BackgroundAnimator.getAnimatedColor(currentScreen, easeGUI$applyDimmingIntensity(startColor));
    }

    @ModifyArg(
            method = "renderBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"),
            index = 5
    )
    private int easeGUI$modifyTransparentBgEndColor(int endColor) {
        Screen currentScreen = (Screen) (Object) this;
        if (!BackgroundAnimator.shouldAnimateBackground(currentScreen)) {
            return endColor;
        }
        return BackgroundAnimator.getAnimatedColor(currentScreen, easeGUI$applyDimmingIntensity(endColor));
    }

    @Unique
    private static int easeGUI$applyDimmingIntensity(int color) {
        float multiplier = ConfigManager.getConfig().global.dimmingIntensity / 0.50f;

        int alpha = (color >> 24) & 0xFF;
        int targetAlpha = Math.min(Math.max(Math.round(alpha * multiplier), 0), 255);
        return (targetAlpha << 24) | (color & 0x00FFFFFF);
    }
}