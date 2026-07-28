package net.weyne1.easegui.client.mixin.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.weyne1.easegui.client.animator.LogoAnimator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LogoRenderer.class, priority = 1010)
public class LogoRendererMixin {

    @Shadow @Final private boolean showEasterEgg;
    @Shadow @Final private boolean keepLogoThroughFade;

    @Inject(
            method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IFI)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void easeGUI$overrideLogo(GuiGraphicsExtractor graphics, int width, float alpha, int heightOffset, CallbackInfo ci) {
        if (LogoAnimator.render(graphics, width, alpha, heightOffset, showEasterEgg, keepLogoThroughFade)) {
            ci.cancel();
        }
    }
}