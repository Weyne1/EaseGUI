package net.weyne1.easegui.client.mixin.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.animator.ContainerAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {

    protected AbstractContainerScreenMixin(Component title) { super(title); }

    @Unique private boolean easeGUI$isContainerAnimatedInCurrentFrame = false;

    @Inject(
            method = "renderBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V")
    )
    private void easeGUI$onPreRenderContainer(GuiGraphics gg, int mx, int my, float pt, CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        if (ContainerAnimator.preRender(screen, gg)) {
            this.easeGUI$isContainerAnimatedInCurrentFrame = true;
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void easeGUI$onPostRenderContainer(GuiGraphics gg, int mx, int my, float pt, CallbackInfo ci) {
        if (this.easeGUI$isContainerAnimatedInCurrentFrame) {
            ContainerAnimator.postRender(gg);
            this.easeGUI$isContainerAnimatedInCurrentFrame = false;
        }
    }
}