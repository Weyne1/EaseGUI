package net.weyne1.easegui.client.mixin.gui;

import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.weyne1.easegui.client.animation.AnimationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("ModifyVariableMayUseName")
@Mixin(GuiTextRenderState.class)
public class GuiTextRenderStateMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private static int easeGUI$modifyTextStateColor(int color) {
        if (!AnimationContext.isActive()) return color;
        return net.weyne1.easegui.client.util.ColorUtils.getAnimatedColor(color);
    }
}