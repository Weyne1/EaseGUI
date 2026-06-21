package net.weyne1.easegui.client.mixin.advancements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.weyne1.easegui.client.animator.AdvancementsAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementsScreen.class)
public class AdvancementsScreenMixin {

    @Unique
    private boolean easeGUI$isWindowAnimated = false;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementsScreen;renderInside(Lnet/minecraft/client/gui/GuiGraphics;IIII)V"
            )
    )
    private void easeGUI$preRenderWindow(GuiGraphics gg, int mx, int my, float pt, CallbackInfo ci) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;
        if (AdvancementsAnimator.preRenderWindow(screen, gg)) {
            this.easeGUI$isWindowAnimated = true;
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void easeGUI$postRenderWindow(GuiGraphics gg, int mx, int my, float pt, CallbackInfo ci) {
        if (this.easeGUI$isWindowAnimated) {
            AdvancementsAnimator.postRenderWindow(gg);
            this.easeGUI$isWindowAnimated = false;
        }
    }
}