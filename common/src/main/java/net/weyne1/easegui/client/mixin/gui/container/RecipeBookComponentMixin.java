package net.weyne1.easegui.client.mixin.gui.container;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.weyne1.easegui.client.extension.RecipeBookComponentExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookComponentMixin extends RecipeBookComponentExtension {

    @Invoker("getXOrigin")
    int easegui$getXOrigin();

    @Invoker("getYOrigin")
    int easegui$getYOrigin();
}