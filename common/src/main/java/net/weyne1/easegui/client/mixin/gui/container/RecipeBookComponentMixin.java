package net.weyne1.easegui.client.mixin.gui.container;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.weyne1.easegui.client.accessor.RecipeBookAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookComponentMixin implements RecipeBookAccessor {
    @Shadow private boolean visible;
    @Shadow private int xOffset;

    @Accessor("IMAGE_WIDTH")
    public static int easeGUI$getVanillaWidth() {
        throw new AssertionError();
    }

    @Accessor("IMAGE_HEIGHT")
    public static int easeGUI$getVanillaHeight() {
        throw new AssertionError();
    }

    @Override public boolean easeGUI$isVisible() { return this.visible; }
    @Override public int easeGUI$getXOffset() { return this.xOffset; }

    @Override public int easeGUI$getBookWidth() { return easeGUI$getVanillaWidth(); }
    @Override public int easeGUI$getBookHeight() { return easeGUI$getVanillaHeight(); }
}