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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Screen.class)
@SuppressWarnings("ConstantConditions")
public abstract class ScreenMixin {

    @Shadow @Nullable protected Minecraft minecraft;

    // Container lifecycle

    @WrapMethod(method = "renderWithTooltip")
    private void easeGUI$wrapScreenRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        if (RenderSystem.isOnRenderThread()
                && this instanceof ContainerScreenExtension
                && !AnimationContext.isAnimationDisabled()) {
            try (AnimationScope ignored = ContainerAnimator.beginAnimation((Screen) (Object) this, guiGraphics)) {
                AnimationContext.pushParentAnimation();
                try {
                    original.call(guiGraphics, mouseX, mouseY, partialTick);
                } finally {
                    AnimationContext.popParentAnimation();
                }
            }
        } else {
            original.call(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    // Menu background

    @WrapMethod(method = "renderBackground")
    private void easeGUI$wrapBackground(GuiGraphics guiGraphics, Operation<Void> original) {
        AnimationScope currentScope = AnimationContext.getCurrentScope();
        if (currentScope != null) currentScope.suspend();

        try {
            Screen currentScreen = (Screen) (Object) this;
            boolean blurBackground = ConfigManager.getConfig().global.blurBackground;
            boolean enableSmoothBlur = ConfigManager.getConfig().global.enableSmoothFade;

            boolean isRealScreenFrame = this.minecraft != null && this.minecraft.level != null
                    && this.minecraft.screen == (Object) this;

            if (isRealScreenFrame && blurBackground) {
                try {
                    guiGraphics.flush();
                    float progress = enableSmoothBlur && BackgroundAnimator.shouldAnimateBackground(currentScreen) ? ScreenAnimationTracker.getProgress() : 1.0f;
                    float partialTick = this.minecraft.getFrameTime();

                    Blur.renderBlur(progress, partialTick);
                } catch (Exception ignored) {
                }
            }

            try (AnimationScope ignored = BackgroundAnimator.beginRenderMenu(currentScreen, guiGraphics)) {
                original.call(guiGraphics);
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
        return BackgroundAnimator.getAnimatedColor(currentScreen, startColor);
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
        return BackgroundAnimator.getAnimatedColor(currentScreen, endColor);
    }
}