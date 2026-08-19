package net.weyne1.easegui.client.mixin.screens;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.BookSignScreen;
import net.weyne1.easegui.client.extension.WidgetExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookSignScreen.class)
public abstract class BookSignScreenMixin {

    @Shadow private EditBox titleBox;

    // Marks the EditBox for text input as non-animable
    @Inject(method = "init", at = @At("TAIL"))
    private void easeGUI$excludeTitleBox(CallbackInfo ci) {
        if (this.titleBox != null) {
            ((WidgetExtension) this.titleBox).easeGUI$setExcluded(true);
        }
    }
}