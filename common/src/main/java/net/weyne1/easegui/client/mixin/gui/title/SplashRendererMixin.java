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
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/ActiveTextCollector;accept(Lnet/minecraft/client/gui/TextAlignment;IILnet/minecraft/client/gui/ActiveTextCollector$Parameters;Lnet/minecraft/network/chat/Component;)V"
            )
    )
    private void easeGUI$animateSplash(
            ActiveTextCollector collector,
            TextAlignment textAlignment,
            int x,
            int y,
            ActiveTextCollector.Parameters parameters,
            Component text,
            Operation<Void> original
    ) {
        ActiveTextCollector.Parameters animatedParams = SplashAnimator.getAnimatedParameters(parameters);

        if (animatedParams != null) {
            original.call(collector, textAlignment, x, y, animatedParams, text);
        }
    }
}