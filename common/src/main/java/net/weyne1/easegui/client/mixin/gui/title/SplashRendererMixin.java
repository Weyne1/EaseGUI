package net.weyne1.easegui.client.mixin.gui.title;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.SplashAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SplashRenderer.class)
public class SplashRendererMixin {

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V")
    )
    private void easeGUI$wrapSplash(GuiGraphics gg, Font font, String text, int x, int y, int color, Operation<Void> original) {
        AnimationScope scope = SplashAnimator.beginRender(gg, color);

        if (scope != null) {
            try (scope) {
                original.call(gg, font, text, x, y, color);
            }
        } else {
            original.call(gg, font, text, x, y, color);
        }
    }
}