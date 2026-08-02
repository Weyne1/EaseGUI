package net.weyne1.easegui.client.mixin.gui.title;

import net.minecraft.client.gui.GuiGraphics;
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

    @Inject(
            method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void easeGUI$overrideLogo(GuiGraphics guiGraphics, int screenWidth, float transparency, int height, CallbackInfo ci) {
        if (LogoAnimator.render(guiGraphics, screenWidth, height, showEasterEgg)) {
            ci.cancel();
        }
    }
}