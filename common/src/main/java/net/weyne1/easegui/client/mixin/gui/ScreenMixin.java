package net.weyne1.easegui.client.mixin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.accessor.ContainerScreenAccessor;
import net.weyne1.easegui.client.accessor.ScreenAnimationAccessor;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.BackgroundAnimator;
import net.weyne1.easegui.client.animator.ContainerAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects custom animation behavior into the global Screen rendering pipeline.
 * Manages container transition lifecycles, menu textures, and background dimming.
 */
@Mixin(Screen.class)
public class ScreenMixin implements ScreenAnimationAccessor {

    @Unique private AnimationScope easeGUI$containerScreenScope = null;
    @Unique private AnimationScope easeGUI$menuBackgroundScope = null;

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

    /**
     * Initializes the container animation scope at the absolute beginning of the screen
     * rendering pipeline, ensuring both widgets and late-rendered tooltips are captured.
     */
    @Inject(method = "renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("HEAD"))
    private void easeGUI$beforeScreenRenderWithTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (RenderSystem.isOnRenderThread() && this instanceof ContainerScreenAccessor) {
            if (this.easeGUI$containerScreenScope != null) {
                ContainerAnimator.closeScope(this.easeGUI$containerScreenScope);
            }

            this.easeGUI$containerScreenScope = ContainerAnimator.beginScreenAnimation((Screen) (Object) this, guiGraphics);
        }
    }

    /**
     * Safely collapses and closes the container animation scope at the end of the rendering
     * pipeline to prevent matrix stack underflows.
     */
    @Inject(method = "renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("RETURN"))
    private void easeGUI$afterScreenRenderWithTooltip(CallbackInfo ci) {
        if (RenderSystem.isOnRenderThread() && this.easeGUI$containerScreenScope != null) {
            ContainerAnimator.closeScope(this.easeGUI$containerScreenScope);
            this.easeGUI$containerScreenScope = null;
        }
    }

    // BACKGROUND RENDERING ISOLATION (SUSPEND / RESUME)

    /**
     * Suspends active container scaling/translation right before the transparent background
     * gradient is drawn, keeping the background tint absolute and static.
     */
    @Inject(method = "renderTransparentBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"))
    private void easeGUI$suspendBeforeTransparentBackground(CallbackInfo ci) {
        if (this.easeGUI$containerScreenScope != null) {
            this.easeGUI$containerScreenScope.suspend();
        }
    }

    /**
     * Resumes the container animation transformations immediately after the transparent
     * background gradient has finished rendering.
     */
    @Inject(method = "renderTransparentBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("RETURN"))
    private void easeGUI$resumeAfterTransparentBackground(CallbackInfo ci) {
        if (this.easeGUI$containerScreenScope != null) {
            this.easeGUI$containerScreenScope.resume();
        }
    }

    /**
     * Isolates the main menu background rendering from container transformations and
     * handles standalone main-menu background animations.
     */
    @Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"))
    private void easeGUI$preRenderMenuBackground(GuiGraphics partialTick, CallbackInfo ci) {
        if (this.easeGUI$containerScreenScope != null) {
            this.easeGUI$containerScreenScope.suspend();
        }

        if (this.easeGUI$menuBackgroundScope != null) {
            this.easeGUI$menuBackgroundScope.close();
        }

        if (!easeGUI$isWorldLoadingScreen() && BackgroundAnimator.shouldAnimate()) {
            this.easeGUI$menuBackgroundScope = BackgroundAnimator.beginRenderMenu(partialTick);
        }
    }

    /**
     * Cleans up menu background scope and restores the container animation transforms
     * for subsequent interface elements.
     */
    @Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("RETURN"))
    private void easeGUI$postRenderMenuBackground(CallbackInfo ci) {
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
        if (easeGUI$isWorldLoadingScreen()) {
            return originalColor;
        }
        return BackgroundAnimator.getAnimatedColor(originalColor);
    }

    /**
     * Applies animated alpha to the bottom color of the Screen background gradient.
     */
    @ModifyArg(
            method = "renderTransparentBackground(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"),
            index = 5
    )
    private int easeGUI$modifyTransparentBgBottomColor(int originalColor) {
        if (easeGUI$isWorldLoadingScreen()) {
            return originalColor;
        }
        return BackgroundAnimator.getAnimatedColor(originalColor);
    }
}