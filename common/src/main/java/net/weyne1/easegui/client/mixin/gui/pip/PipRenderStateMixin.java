package net.weyne1.easegui.client.mixin.gui.pip;

import net.minecraft.client.renderer.state.gui.pip.*;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.extension.PipExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({
        GuiSkinRenderState.class,
        GuiEntityRenderState.class,
        GuiBookModelRenderState.class,
        GuiBannerResultRenderState.class,
        OversizedItemRenderState.class
})
public abstract class PipRenderStateMixin implements PipExtension {

    @Unique private float easegui$alpha = 1.0f;

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void easeGUI$captureAlpha(CallbackInfo ci) {
        if (AnimationContext.isActive()) {
            this.easegui$alpha = AnimationContext.getCurrentAlpha();
        }
    }

    @Override
    public float easegui$getAlpha() {
        return this.easegui$alpha;
    }
}