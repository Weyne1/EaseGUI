package net.weyne1.easegui.client.mixin.renderer;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void easeGUI$onFrameStart(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        ScreenStateTracker.incrementFrame();
        AnimationContext.resetFrameState();
    }
}