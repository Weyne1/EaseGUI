package net.weyne1.easegui.client.mixin.blaze3d;

import com.mojang.blaze3d.systems.RenderSystem;
import net.weyne1.easegui.client.state.RenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    /**
     * Prevents changes to opacity during animations.
     */
    @ModifyVariable(method = "setShaderColor", at = @At("HEAD"), argsOnly = true, ordinal = 3, remap = false)
    private static float forceShaderAlpha(float alpha) {
        if (RenderContext.isAnimatingWidget()) {
            return RenderContext.getCurrentAlpha();
        }
        return alpha;
    }

    /**
     * Prevents blending from being disabled during animations.
     */
    @Inject(method = "disableBlend", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preventDisableBlend(CallbackInfo ci) {
        if (RenderContext.isAnimatingWidget() && RenderContext.getCurrentAlpha() < 1.0f) {
            ci.cancel();
        }
    }
}