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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Screen.class)
@SuppressWarnings("ConstantConditions")
public abstract class ScreenMixin {

    @Final @Shadow protected Minecraft minecraft;
    @Shadow protected abstract void extractBlurredBackground(GuiGraphicsExtractor graphics);

    // Container lifecycle

    @WrapMethod(method = "extractRenderStateWithTooltipAndSubtitles")
    private void easeGUI$wrapScreenRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
        if (RenderSystem.isOnRenderThread()
                && this instanceof ContainerScreenExtension
                && !AnimationContext.isAnimationDisabled()) {
            try (AnimationScope ignored = ContainerAnimator.beginAnimation((Screen) (Object) this, graphics)) {
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

    // Transparent background (blur / dim)

    @WrapMethod(method = "extractTransparentBackground")
    private void easeGUI$wrapTransparentBackground(GuiGraphicsExtractor graphics, Operation<Void> original) {
        AnimationScope currentScope = AnimationContext.getCurrentScope();
        if (currentScope != null) currentScope.suspend();

        try {
            Screen currentScreen = (Screen) (Object) this;
            boolean blurContainers = ConfigManager.getConfig().global.blurContainers;

            if (this.minecraft != null && this.minecraft.level != null && this.minecraft.gui.screen() == (Object) this
                    && blurContainers && BackgroundAnimator.shouldAnimateBackground(currentScreen)) {
                this.extractBlurredBackground(graphics);
            }

            original.call(graphics);
        } finally {
            if (currentScope != null) currentScope.resume();
        }
    }

    // Menu background

    @WrapMethod(method = "extractMenuBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V")
    private void easeGUI$wrapMenuBackground(GuiGraphicsExtractor graphics, Operation<Void> original) {
        AnimationScope currentScope = AnimationContext.getCurrentScope();
        if (currentScope != null) currentScope.suspend();

        try (AnimationScope ignored = BackgroundAnimator.beginRenderMenu((Screen) (Object) this, graphics)) {
            original.call(graphics);
        } finally {
            if (currentScope != null) currentScope.resume();
        }
    }

    // Gradient color

    @ModifyArgs(
            method = "extractTransparentBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fillGradient(IIIIII)V")
    )
    private void easeGUI$modifyTransparentBgColors(Args args) {
        Screen currentScreen = (Screen) (Object) this;
        if (!BackgroundAnimator.shouldAnimateBackground(currentScreen)) {
            return;
        }

        float intensityMultiplier = ConfigManager.getConfig().global.dimmingIntensity / 0.50f;

        int color1 = easeGUI$applyDimmingIntensity(args.get(4), intensityMultiplier);
        int color2 = easeGUI$applyDimmingIntensity(args.get(5), intensityMultiplier);

        args.set(4, BackgroundAnimator.getAnimatedColor(currentScreen, color1));
        args.set(5, BackgroundAnimator.getAnimatedColor(currentScreen, color2));
    }

    @Unique
    private static int easeGUI$applyDimmingIntensity(int color, float multiplier) {
        int alpha = (color >> 24) & 0xFF;
        int targetAlpha = Math.clamp(Math.round(alpha * multiplier), 0, 255);
        return (targetAlpha << 24) | (color & 0x00FFFFFF);
    }
}