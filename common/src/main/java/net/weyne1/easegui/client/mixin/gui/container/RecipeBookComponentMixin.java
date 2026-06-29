package net.weyne1.easegui.client.mixin.gui.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.weyne1.easegui.client.accessor.RecipeBookAccessor;
import net.weyne1.easegui.client.animator.ContainerAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin implements RecipeBookAccessor {
    @Shadow private boolean visible;
    @Shadow private int xOffset;
    @Shadow private int width;
    @Shadow private int height;

    @Inject(method = "init", at = @At("RETURN"))
    private void easeGUI$onInit(int width, int height, Minecraft minecraft, boolean widthTooNarrow, RecipeBookMenu<?, ?> menu, CallbackInfo ci) {
        ContainerAnimator.registerBook(minecraft.screen, this);
    }

    @Override public boolean easeGUI$isVisible() { return this.visible; }
    @Override public int easeGUI$getXOffset() { return this.xOffset; }
    @Override public int easeGUI$getScreenWidth() { return this.width; }
    @Override public int easeGUI$getScreenHeight() { return this.height; }
}