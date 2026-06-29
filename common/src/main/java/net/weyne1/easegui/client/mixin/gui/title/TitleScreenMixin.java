package net.weyne1.easegui.client.mixin.gui.title;

import net.minecraft.client.gui.screens.TitleScreen;
import net.weyne1.easegui.client.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    /**
     * Cancels the vanilla TitleScreen fadeWidgets animation if EaseGUI is enabled for this screen.
     * This allows EaseGUI to fully control widget fade-in animation,
     * while preserving vanilla transitions if the screen animation is disabled.
     */
    @Inject(method = "fadeWidgets", at = @At("HEAD"), cancellable = true)
    private void easeGUI$cancelFade(CallbackInfo ci) {
        var titleSettings = ConfigManager.getConfig().screens.get("title");

        if (titleSettings == null || !titleSettings.enabled) {
            return;
        }

        ci.cancel();
    }
}