package net.weyne1.easegui.client.mixin.gui;

import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.extension.EaseGuiItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiItemRenderState.class)
public class GuiItemRenderStateMixin implements EaseGuiItemExtension {
    @Unique
    private float easegui$alpha = 1.0f;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void easeGUI$captureAlphaOnCreation(CallbackInfo ci) {
        if (AnimationContext.isActive()) {
            this.easegui$alpha = AnimationContext.getCurrentAlpha();
        }
    }

    @Override
    public float easegui$getAlpha() {
        return this.easegui$alpha;
    }
}