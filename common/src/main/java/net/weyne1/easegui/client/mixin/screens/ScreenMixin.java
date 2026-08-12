package net.weyne1.easegui.client.mixin.screens;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.extension.ContainerScreenExtension;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.animator.ContainerAnimator;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Screen.class)
@SuppressWarnings("ConstantConditions")
public abstract class ScreenMixin {

    @Final @Shadow protected Minecraft minecraft;
    @Shadow protected abstract void extractBlurredBackground(GuiGraphicsExtractor graphics);

    // Container lifecycle
    @WrapMethod(method = "extractRenderStateWithTooltipAndSubtitles")
    private void easeGUI$wrapScreenRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
        ScreenStateTracker.markScreenRendered((Screen) (Object) this);

        if (RenderSystem.isOnRenderThread()
                && this instanceof ContainerScreenExtension
                && !AnimationContext.isAnimationDisabled()) {
            try (AnimationScope ignored = ContainerAnimator.beginContainer((Screen) (Object) this, graphics)) {
                AnimationContext.pushParentAnimation();
                try {
                    original.call(graphics, mouseX, mouseY, a);
                } finally {
                    AnimationContext.popParentAnimation();
                }
            }
        } else {
            original.call(graphics, mouseX, mouseY, a);
        }
    }

    // Transparent background blur
    @WrapMethod(method = "extractTransparentBackground")
    private void easeGUI$wrapTransparentBackground(GuiGraphicsExtractor graphics, Operation<Void> original) {
        AnimationScope currentScope = AnimationContext.getCurrentScope();
        if (currentScope.isAnimating()) {
            currentScope.suspend();
        }

        try {
            Screen currentScreen = (Screen) (Object) this;
            boolean isRealFrame = this.minecraft.level != null && this.minecraft.gui.screen() == currentScreen;
            boolean blurEnabled = ConfigManager.getConfig().global.blurAllTransparentScreens;

            if (isRealFrame && blurEnabled && BackgroundAnimator.isBackgroundEffectAllowed(currentScreen)) {
                this.extractBlurredBackground(graphics);
            }

            original.call(graphics);
        } finally {
            if (currentScope.isAnimating()) {
                currentScope.resume();
            }
        }
    }

    // Menu background
    @WrapMethod(method = "extractMenuBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V")
    private void easeGUI$wrapMenuBackground(GuiGraphicsExtractor graphics, Operation<Void> original) {
        AnimationScope currentScope = AnimationContext.getCurrentScope();
        if (currentScope.isAnimating()) {
            currentScope.suspend();
        }

        try (AnimationScope ignored = BackgroundAnimator.beginRenderMenu((Screen) (Object) this, graphics)) {
            original.call(graphics);
        } finally {
            if (currentScope.isAnimating()) {
                currentScope.resume();
            }
        }
    }

    // Blur tracking
    @Inject(method = "extractBlurredBackground", at = @At("HEAD"))
    private void easeGUI$onExtractBlurredBackground(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        ScreenStateTracker.markBlurredThisFrame();
    }

    // Background dimming intensity
    @ModifyArgs(
            method = "extractTransparentBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fillGradient(IIIIII)V")
    )
    private void easeGUI$modifyTransparentBgColors(Args args) {
        Screen currentScreen = (Screen) (Object) this;
        if (!BackgroundAnimator.isBackgroundEffectAllowed(currentScreen)) {
            return;
        }

        float intensity = ConfigManager.getConfig().global.backgroundDimmingIntensity;

        int color1 = easeGUI$applyDimmingIntensity(args.get(4), intensity);
        int color2 = easeGUI$applyDimmingIntensity(args.get(5), intensity);

        args.set(4, BackgroundAnimator.getAnimatedColor(currentScreen, color1));
        args.set(5, BackgroundAnimator.getAnimatedColor(currentScreen, color2));
    }

    @Unique
    private static int easeGUI$applyDimmingIntensity(int color, float intensity) {
        int targetAlpha = Math.clamp(Math.round(intensity * 255.0f), 0, 255);
        return (targetAlpha << 24) | (color & 0x00FFFFFF);
    }
}