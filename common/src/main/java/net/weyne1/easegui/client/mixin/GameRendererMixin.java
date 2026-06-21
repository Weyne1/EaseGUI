package net.weyne1.easegui.client.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.weyne1.easegui.client.state.ScreenAnimationTracker;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyVariable(method = "processBlurEffect", at = @At(value = "CONSTANT", args = "stringValue=Radius"), ordinal = 0, argsOnly = true)
    private float easeGUI$animateBlurRadius(float originalRadius) {
        return originalRadius * ScreenAnimationTracker.getProgress();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onFrameStart(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        ScreenStateTracker.incrementFrame();
    }
}