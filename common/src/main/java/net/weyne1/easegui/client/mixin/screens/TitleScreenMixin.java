package net.weyne1.easegui.client.mixin.screens;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import net.weyne1.easegui.client.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Shadow private boolean fading;


    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void easegui$disableVanillaFading(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        var titleSettings = ConfigManager.getConfig().screens.get("title");

        if (titleSettings != null && titleSettings.enabled) {
            this.fading = false;
        }
    }

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/LogoRenderer;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IF)V"
            )
    )
    private void easegui$conditionallyFadeLogo(LogoRenderer instance, GuiGraphicsExtractor graphics, int width, float alpha, Operation<Void> original) {
        var titleSettings = ConfigManager.getConfig().screens.get("title");

        if (titleSettings != null && titleSettings.enabled) {
            original.call(instance, graphics, width, 1.0F);
            return;
        }

        original.call(instance, graphics, width, alpha);
    }

    // The game does not provide a reliable way to animate the Realms
    // notification overlay together with its parent button.
    // These icons are therefore hidden while the title screen animation is enabled.
    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/realmsclient/gui/screens/RealmsNotificationsScreen;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"
            )
    )
    private void easegui$suppressRealmsNotifications(RealmsNotificationsScreen instance, GuiGraphicsExtractor graphics, int xm, int ym, float a, Operation<Void> original
    ) {
        var titleSettings = ConfigManager.getConfig().screens.get("title");

        if (titleSettings != null && titleSettings.enabled) {
            return;
        }

        original.call(instance, graphics, xm, ym, a);
    }
}