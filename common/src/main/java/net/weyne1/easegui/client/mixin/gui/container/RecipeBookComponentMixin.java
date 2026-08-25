package net.weyne1.easegui.client.mixin.gui.container;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.weyne1.easegui.client.extension.RecipeBookExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookComponentMixin implements RecipeBookExtension {
    @Shadow private boolean visible;
    @Shadow private int xOffset;

    @Accessor("IMAGE_WIDTH")
    public static int easegui$getVanillaWidth() {
        throw new AssertionError();
    }

    @Accessor("IMAGE_HEIGHT")
    public static int easegui$getVanillaHeight() {
        throw new AssertionError();
    }

    @Override
    public boolean easegui$isVisible() {
        return this.visible;
    }

    @Override
    public int easegui$getXOffset() {
        return this.xOffset;
    }

    @Override
    public int easegui$getBookWidth() {
        return easegui$getVanillaWidth();
    }

    @Override
    public int easegui$getBookHeight() {
        return easegui$getVanillaHeight();
    }
}