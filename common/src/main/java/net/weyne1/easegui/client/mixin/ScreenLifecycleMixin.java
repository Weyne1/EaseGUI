package net.weyne1.easegui.client.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenLifecycleMixin {

    @Inject(method = {"init(Lnet/minecraft/client/Minecraft;II)V", "rebuildWidgets"}, at = @At("HEAD"))
    private void onScreenUpdate(CallbackInfo ci) {
        ScreenStateTracker.markScreenOpened();
    }
}