package net.weyne1.easegui.client.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.state.RenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    /**
     * Forces animated alpha during widget rendering by overriding
     * the alpha argument of GuiGraphics#setColor.
     */
    @ModifyVariable(method = "setColor", at = @At("HEAD"), argsOnly = true, ordinal = 3)
    private float forceGuiAlpha(float alpha) {
        if (RenderContext.isAnimatingWidget()) {
            return RenderContext.getCurrentAlpha();
        }
        return alpha;
    }
}