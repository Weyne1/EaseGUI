package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.accessor.ContainerScreenAccessor;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.animator.ContainerAnimator;
import net.weyne1.easegui.client.config.ConfigManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Screen.class)
@SuppressWarnings("ConstantConditions")
public abstract class ScreenMixin {

    @Final @Shadow protected Minecraft minecraft;
    @Shadow protected abstract void renderBlurredBackground(GuiGraphics guiGraphics);

    // Container lifecycle

    @WrapMethod(method = "renderWithTooltipAndSubtitles")
    private void easeGUI$wrapScreenRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        if (RenderSystem.isOnRenderThread() && this instanceof ContainerScreenAccessor) {

            try (AnimationScope ignored = ContainerAnimator.beginAnimation((Screen) (Object) this, guiGraphics)) {
                AnimationContext.pushParentAnimation();

                original.call(guiGraphics, mouseX, mouseY, partialTick);

                AnimationContext.popParentAnimation();
            }

        } else {
            original.call(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    // Transparent background (blur / dim)

    @WrapMethod(method = "renderTransparentBackground")
    private void easeGUI$wrapTransparentBackground(GuiGraphics guiGraphics, Operation<Void> original) {
        AnimationScope currentScope = AnimationContext.getCurrentScope();
        if (currentScope != null) currentScope.suspend();

        try {
            Screen currentScreen = (Screen) (Object) this;
            boolean blurContainers = ConfigManager.getConfig().global.blurContainers;

            if (this.minecraft != null && this.minecraft.level != null && blurContainers && BackgroundAnimator.shouldAnimateBackground(currentScreen)) {
                this.renderBlurredBackground(guiGraphics);
            }

            original.call(guiGraphics);
        } finally {
            if (currentScope != null) currentScope.resume();
        }
    }

    // Menu background

    @WrapMethod(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V")
    private void easeGUI$wrapMenuBackground(GuiGraphics guiGraphics, Operation<Void> original) {
        AnimationScope currentScope = AnimationContext.getCurrentScope();
        if (currentScope != null) currentScope.suspend();

        try (AnimationScope ignored = BackgroundAnimator.beginRenderMenu((Screen) (Object) this, guiGraphics)) {
            original.call(guiGraphics);
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
        if (BackgroundAnimator.isLoadingScreen(currentScreen)) {
            return;
        }

        args.set(4, BackgroundAnimator.getAnimatedColor(currentScreen, args.get(4)));
        args.set(5, BackgroundAnimator.getAnimatedColor(currentScreen, args.get(5)));
    }
}