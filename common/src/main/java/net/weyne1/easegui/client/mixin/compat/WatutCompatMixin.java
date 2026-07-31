package net.weyne1.easegui.client.mixin.compat;

import net.weyne1.easegui.client.animation.AnimationContext;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Provides clean screen frames without animation for WATUT.
 */
@Pseudo
@Mixin(targets = "com.corosus.watut.client.screen.RenderHelper", remap = false)
public class WatutCompatMixin {

    @Dynamic
    @Inject(method = {"renderWithTooltipEnd", "guiRender"}, at = @At("HEAD"), require = 0)
    private static void easeGUI$disableStart(CallbackInfo ci) {
        AnimationContext.beginManualDisable();
    }

    @Dynamic
    @Inject(method = {"renderWithTooltipEnd", "guiRender"}, at = @At("RETURN"), require = 0)
    private static void easeGUI$disableEnd(CallbackInfo ci) {
        AnimationContext.endManualDisable();
    }
}