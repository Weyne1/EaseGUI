package net.weyne1.easegui.client.mixin.screens;

import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.state.ScreenStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenLifecycleMixin {

    @Inject(method = "added()V", at = @At("HEAD"))
    private void easegui$onScreenAdded(CallbackInfo ci) {
        Screen currentScreen = (Screen) (Object) this;

        if (ScreenStateTracker.checkAndTrackNewScreen(currentScreen)) {
            ScreenStateTracker.markScreenOpened();
        }
    }
}