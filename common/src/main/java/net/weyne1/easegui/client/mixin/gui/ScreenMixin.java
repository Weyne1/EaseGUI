package net.weyne1.easegui.client.mixin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.accessor.ContainerScreenAccessor;
import net.weyne1.easegui.client.accessor.ScreenAnimationAccessor;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Screen.class)
public abstract class ScreenMixin implements ScreenAnimationAccessor {

    @Unique private AnimationScope easeGUI$containerScreenScope = null;
    @Unique private AnimationScope easeGUI$menuBackgroundScope = null;

    @Final @Shadow protected Minecraft minecraft;
    @Shadow protected abstract void renderBlurredBackground(GuiGraphics guiGraphics);

    @Override
    @Unique
    public AnimationScope easeGUI$getContainerScope() {
        return this.easeGUI$containerScreenScope;
    }

    // Container lifecycle

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("HEAD"))
    private void easeGUI$beforeScreenRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (RenderSystem.isOnRenderThread() && this instanceof ContainerScreenAccessor) {
            if (this.easeGUI$containerScreenScope != null) {
                this.easeGUI$containerScreenScope.close();
                this.easeGUI$containerScreenScope = null;
            }
            this.easeGUI$containerScreenScope = ContainerAnimator.beginAnimation((Screen) (Object) this, guiGraphics);

            if (this.easeGUI$containerScreenScope != null) {
                AnimationContext.pushParentAnimation();
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("RETURN"))
    private void easeGUI$afterScreenRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (RenderSystem.isOnRenderThread() && this.easeGUI$containerScreenScope != null) {
            this.easeGUI$containerScreenScope.close();
            this.easeGUI$containerScreenScope = null;

            if (AnimationContext.hasParentAnimation()) {
                AnimationContext.popParentAnimation();
            }
        }
    }

    // Transparent background (blur / dim)

    @Inject(method = "renderTransparentBackground", at = @At("HEAD"))
    private void easeGUI$suspendBeforeTransparentBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.easeGUI$containerScreenScope != null) {
            this.easeGUI$containerScreenScope.suspend();
        }

        Screen currentScreen = (Screen) (Object) this;
        boolean blurContainers = ConfigManager.getConfig().global.blurContainers;
        if (this.minecraft != null && this.minecraft.level != null && blurContainers && BackgroundAnimator.shouldAnimateBackground(currentScreen)) {
            this.renderBlurredBackground(guiGraphics);
        }
    }

    @Inject(method = "renderTransparentBackground", at = @At("RETURN"))
    private void easeGUI$resumeAfterTransparentBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.easeGUI$containerScreenScope != null) {
            this.easeGUI$containerScreenScope.resume();
        }
    }

    // Menu background

    @Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"))
    private void easeGUI$preRenderMenuBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.easeGUI$containerScreenScope != null) {
            this.easeGUI$containerScreenScope.suspend();
        }

        if (this.easeGUI$menuBackgroundScope != null) {
            this.easeGUI$menuBackgroundScope.close();
        }

        this.easeGUI$menuBackgroundScope = BackgroundAnimator.beginRenderMenu((Screen) (Object) this, guiGraphics);
    }

    @Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("RETURN"))
    private void easeGUI$postRenderMenuBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.easeGUI$menuBackgroundScope != null) {
            this.easeGUI$menuBackgroundScope.close();
            this.easeGUI$menuBackgroundScope = null;
        }

        if (this.easeGUI$containerScreenScope != null) {
            this.easeGUI$containerScreenScope.resume();
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