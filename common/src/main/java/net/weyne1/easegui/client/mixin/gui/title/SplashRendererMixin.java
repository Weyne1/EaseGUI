package net.weyne1.easegui.client.mixin.gui.title;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.animator.SplashAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SplashRenderer.class)
public class SplashRendererMixin {

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/ActiveTextCollector;accept(Lnet/minecraft/client/gui/TextAlignment;IILnet/minecraft/client/gui/ActiveTextCollector$Parameters;Lnet/minecraft/network/chat/Component;)V"
            )
    )
    private void easeGUI$animateSplash(
            ActiveTextCollector collector,
            TextAlignment alignment,
            int anchorX,
            int y,
            ActiveTextCollector.Parameters parameters,
            Component text,
            Operation<Void> original
    ) {
        if (text == null || text.getString().isBlank()) {
            original.call(collector, alignment, anchorX, y, parameters, text);
            return;
        }

        ActiveTextCollector.Parameters animatedParams = SplashAnimator.getAnimatedParameters(parameters);

        if (animatedParams != null) {
            original.call(collector, alignment, anchorX, y, animatedParams, text);
        }
    }
}