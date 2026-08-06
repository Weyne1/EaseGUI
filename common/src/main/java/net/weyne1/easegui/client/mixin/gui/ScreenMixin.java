package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.extension.ContainerScreenExtension;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.animator.ContainerAnimator;
import net.weyne1.easegui.client.config.ConfigManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Screen.class)
@SuppressWarnings({"ConstantConditions", "NameDoesntMatchTargetClass"})
public abstract class ScreenMixin {

    @Shadow @Nullable protected Minecraft minecraft;
    @Shadow protected abstract void renderBlurredBackground(float partialTick);

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

    // Transparent background (blur / dim)

    @WrapMethod(method = "renderTransparentBackground")
    private void easeGUI$wrapTransparentBackground(GuiGraphics graphics, Operation<Void> original) {
        AnimationScope currentScope = AnimationContext.getCurrentScope();
        if (currentScope != null) currentScope.suspend();

        try {
            Screen currentScreen = (Screen) (Object) this;
            boolean blurContainers = ConfigManager.getConfig().global.blurContainers;

            if (this.minecraft != null && this.minecraft.level != null && this.minecraft.screen == (Object) this
                    && blurContainers && BackgroundAnimator.shouldAnimateBackground(currentScreen))
            {
                float partialTick = this.minecraft.getTimer().getGameTimeDeltaTicks();
                this.renderBlurredBackground(partialTick);
            }

            original.call(graphics);
        } finally {
            if (currentScope != null) currentScope.resume();
        }
    }

    // Menu background

    @WrapMethod(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V")
    private void easeGUI$wrapMenuBackground(GuiGraphics graphics, Operation<Void> original) {
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
            method = "renderTransparentBackground(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V")
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