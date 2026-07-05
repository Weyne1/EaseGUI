package net.weyne1.easegui.client.mixin.gui.container;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.weyne1.easegui.client.accessor.RecipeBookComponentAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookComponentMixin extends RecipeBookComponentAccessor {

    @Accessor("visible")
    boolean easeGUI$isVisible();

    @Invoker("getXOrigin")
    int easeGUI$getXOrigin();

    @Invoker("getYOrigin")
    int easeGUI$getYOrigin();
}