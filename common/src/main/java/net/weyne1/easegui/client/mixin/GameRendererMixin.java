package net.weyne1.easegui.client.mixin;

import net.minecraft.client.renderer.GameRenderer;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.state.ScreenAnimationTracker;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyArg(
            method = "processBlurEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PostChain;setUniform(Ljava/lang/String;F)V"),
            index = 1
    )
    private float easeGUI$animateBlurRadiusArg(float originalRadius) {
        return originalRadius * ScreenAnimationTracker.getProgress();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void easeGUI$onFrameStart(CallbackInfo ci) {
        ScreenStateTracker.incrementFrame();
        AnimationContext.resetFrameState();
    }
}