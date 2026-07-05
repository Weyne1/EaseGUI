package net.weyne1.easegui.client.mixin.gui.title;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.TitleScreen;
import net.weyne1.easegui.client.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    /**
     * Cancels the vanilla TitleScreen fadeWidgets animation if EaseGUI is enabled for this screen.
     * This allows EaseGUI to fully control widget fade-in animation,
     * while preserving vanilla transitions if the screen animation is disabled.
     */
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/TitleScreen;fadeWidgets(F)V"
            )
    )
    private void easeGUI$conditionallyFadeWidgets(TitleScreen instance, float v, Operation<Void> original) {
        var titleSettings = ConfigManager.getConfig().screens.get("title");

        if (titleSettings != null && titleSettings.enabled) {
            return;
        }

        original.call(instance, v);
    }
}