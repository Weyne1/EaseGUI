package net.weyne1.easegui.client.mixin.title;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.weyne1.easegui.client.animator.LogoAnimator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoRenderer.class)
public class LogoRendererMixin {

    @Shadow @Final private boolean showEasterEgg;
    @Shadow @Final private boolean keepLogoThroughFade;

    @Inject(
            method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void easeGUI$overrideLogo(GuiGraphics g, int w, float alpha, int h, CallbackInfo ci) {
        if (LogoAnimator.render(g, w, alpha, h, showEasterEgg, keepLogoThroughFade)) {
            ci.cancel();
        }
    }
}