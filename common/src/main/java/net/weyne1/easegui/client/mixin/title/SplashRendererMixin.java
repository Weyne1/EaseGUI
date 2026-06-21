package net.weyne1.easegui.client.mixin.title;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.weyne1.easegui.client.animator.SplashAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashRenderer.class)
public class SplashRendererMixin {

    @Inject(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V")
    )
    private void easeGUI$beforeDrawSplash(GuiGraphics guiGraphics, int screenWidth, Font font, int color, CallbackInfo ci) {
        if (SplashAnimator.shouldAnimate()) {
            SplashAnimator.preRenderLocal(guiGraphics, color);
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V", shift = At.Shift.AFTER)
    )
    private void easeGUI$afterDrawSplash(GuiGraphics guiGraphics, int screenWidth, Font font, int color, CallbackInfo ci) {
        if (SplashAnimator.shouldAnimate()) {
            SplashAnimator.postRenderLocal(guiGraphics);
        }
    }
}