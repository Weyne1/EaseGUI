package net.weyne1.easegui.client.mixin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.accessor.ContainerScreenAccessor;
import net.weyne1.easegui.client.accessor.ScreenAnimationAccessor;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.animator.ContainerAnimator;
import net.weyne1.easegui.client.compat.WatutCompat;
import net.weyne1.easegui.client.config.ConfigManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin implements ScreenAnimationAccessor {

    @Unique private AnimationScope easeGUI$containerScreenScope = null;
    @Unique private AnimationScope easeGUI$menuBackgroundScope = null;

    @Shadow @Nullable protected Minecraft minecraft;
    @Shadow protected abstract void renderBlurredBackground(float partialTick);

    @Override
    @Unique
    public AnimationScope easeGUI$getContainerScope() {
        return this.easeGUI$containerScreenScope;
    }

    @Unique
    private boolean easeGUI$isWorldLoadingScreen() {
        return BackgroundAnimator.isLoadingScreen((Screen) (Object) this);
    }

    // CONTAINER SCREEN ANIMATION LIFECYCLE

    @Inject(method = "renderWithTooltip", at = @At("HEAD"))
    private void easeGUI$beforeScreenRenderWithTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (WatutCompat.isWatutRendering()) {
            return;
        }

        if (RenderSystem.isOnRenderThread() && this instanceof ContainerScreenAccessor) {
            if (this.easeGUI$containerScreenScope != null) {
                ContainerAnimator.closeScope(this.easeGUI$containerScreenScope);
            }

            this.easeGUI$containerScreenScope = ContainerAnimator.beginScreenAnimation((Screen) (Object) this, guiGraphics);
        }
    }

    @Inject(method = "renderWithTooltip", at = @At("RETURN"))
    private void easeGUI$afterScreenRenderWithTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (RenderSystem.isOnRenderThread() && this.easeGUI$containerScreenScope != null) {
            ContainerAnimator.closeScope(this.easeGUI$containerScreenScope);
            this.easeGUI$containerScreenScope = null;
        }
    }

    // BACKGROUND RENDERING ISOLATION (SUSPEND / RESUME)

    @Inject(method = "renderTransparentBackground", at = @At("HEAD"))
    private void easeGUI$suspendBeforeTransparentBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (WatutCompat.isWatutRendering()) {
            return;
        }

        if (this.easeGUI$containerScreenScope != null) {
            this.easeGUI$containerScreenScope.suspend();
        }

        boolean blurContainers = ConfigManager.getConfig().global.blurContainers;

        //noinspection ConstantValue
        if (this.minecraft != null && this.minecraft.level != null && blurContainers && this.minecraft.screen == (Object) this) {

            float partialTick = this.minecraft.getTimer().getGameTimeDeltaTicks();
            this.renderBlurredBackground(partialTick);
        }
    }

    @Inject(method = "renderTransparentBackground", at = @At("RETURN"))
    private void easeGUI$resumeAfterTransparentBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.easeGUI$containerScreenScope != null) {
            this.easeGUI$containerScreenScope.resume();
        }
    }

    @Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"))
    private void easeGUI$preRenderMenuBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (WatutCompat.isWatutRendering()) {
            return;
        }

        if (this.easeGUI$containerScreenScope != null) {
            this.easeGUI$containerScreenScope.suspend();
        }

        if (this.easeGUI$menuBackgroundScope != null) {
            this.easeGUI$menuBackgroundScope.close();
        }

        if (!easeGUI$isWorldLoadingScreen() && BackgroundAnimator.shouldAnimate()) {
            this.easeGUI$menuBackgroundScope = BackgroundAnimator.beginRenderMenu(guiGraphics);
        }
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

    // BACKGROUND GRADIENT COLOR MODIFICATIONS

    /**
     * Applies animated alpha to the top color of the Screen background gradient.
     */
    @ModifyArg(
            method = "renderTransparentBackground(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"),
            index = 4
    )
    private int easeGUI$modifyTransparentBgTopColor(int originalColor) {
        if (easeGUI$isWorldLoadingScreen() || WatutCompat.isWatutRendering()) {
            return originalColor;
        }
        return BackgroundAnimator.getAnimatedColor(originalColor);
    }

    @ModifyArg(
            method = "renderTransparentBackground(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"),
            index = 5
    )
    private int easeGUI$modifyTransparentBgBottomColor(int originalColor) {
        if (easeGUI$isWorldLoadingScreen() || WatutCompat.isWatutRendering()) {
            return originalColor;
        }
        return BackgroundAnimator.getAnimatedColor(originalColor);
    }
}