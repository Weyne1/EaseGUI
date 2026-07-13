package net.weyne1.easegui.client.mixin.gui.container;

import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.weyne1.easegui.client.extension.RecipeBookScreenExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class AbstractRecipeBookScreenMixin implements RecipeBookScreenExtension {

    @Final
    @Shadow private RecipeBookComponent<?> recipeBookComponent;

    @Override
    public RecipeBookComponent<?> easeGUI$getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
}