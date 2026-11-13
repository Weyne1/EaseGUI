package net.weyne1.easegui.client.mixin.compat;

import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Isolates EMI overlay rendering from container transformations.
 * Prevents item panels, search bars, and recipes from scaling/moving with the container.
 */
@Pseudo
@Mixin(targets = "dev.emi.emi.screen.EmiScreenManager", remap = false)
public class EmiCompatMixin {

    @Dynamic
    @Inject(method = {"render", "drawBackground", "drawForeground"}, at = @At("HEAD"), require = 0)
    private static void easegui$onEmiRenderStart(CallbackInfo ci) {
        AnimationScope scope = AnimationContext.getCurrentScope();
        if (scope.isAnimating()) scope.suspend();
    }

    @Dynamic
    @Inject(method = {"render", "drawBackground", "drawForeground"}, at = @At("RETURN"), require = 0)
    private static void easegui$onEmiRenderEnd(CallbackInfo ci) {
        AnimationScope scope = AnimationContext.getCurrentScope();
        if (scope.isAnimating()) scope.resume();
    }
}