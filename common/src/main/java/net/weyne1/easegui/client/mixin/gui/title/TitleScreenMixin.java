package net.weyne1.easegui.client.mixin.gui.title;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.SplashAnimator;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.mixin.accessor.SplashRendererAccessor;
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

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/SplashRenderer;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;F)V"
            )
    )
    private void easeGUI$wrapSplashRender(SplashRenderer splashRenderer, GuiGraphics guiGraphics, int width, Font font, float fade, Operation<Void> original) {
        var component = ((SplashRendererAccessor) splashRenderer).easeGUI$getSplash();

        int color = 0xFFFF55;
        if (component != null && component.getStyle().getColor() != null) {
            color = component.getStyle().getColor().getValue();
        }

        AnimationScope scope = SplashAnimator.beginRender(guiGraphics, color);

        if (scope != null) {
            try (scope) {
                original.call(splashRenderer, guiGraphics, width, font, fade);
            }
        } else {
            original.call(splashRenderer, guiGraphics, width, font, fade);
        }
    }
}