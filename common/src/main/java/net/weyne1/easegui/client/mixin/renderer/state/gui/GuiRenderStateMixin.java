package net.weyne1.easegui.client.mixin.renderer.state.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

import static net.weyne1.easegui.client.EaseGUIClient.LOGGER;

@Mixin(GuiRenderState.class)
public abstract class GuiRenderStateMixin {
    @Unique
    private static boolean easegui$duplicateBlurWarningLogged = false;

    @Accessor("firstStratumAfterBlur")
    public abstract int easegui$getFirstStratumAfterBlur();

    // Prevents duplicate blur requests from crashing GuiRenderState.
    // Vanilla allows only one blur request per GuiRenderState.
    @WrapMethod(method = "blurBeforeThisStratum")
    private void easegui$guardDuplicateBlur(Operation<Void> original) {
        if (easegui$getFirstStratumAfterBlur() != Integer.MAX_VALUE) {
            if (!easegui$duplicateBlurWarningLogged) {
                LOGGER.warn("Suppressed duplicate blur request. " +
                        "Multiple blur requests in the same GuiRenderState are not supported.");
                easegui$duplicateBlurWarningLogged = true;
            }
            return;
        }

        original.call();
    }
}