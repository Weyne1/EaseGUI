package net.weyne1.easegui.client.mixin.gui.title;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.TitleScreen;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Unique
    private static boolean easeGUI$usesCustomTitleAnimation() {
        EaseGUIConfig config = ConfigManager.getConfig();
        if (!config.global.enabled) {
            return false;
        }

        var titleSettings = config.screens.get("title");
        return titleSettings != null && titleSettings.enabled;
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 2))
    private float easeGUI$skipVanillaTitleElementFade(float value, float min, float max, Operation<Float> original) {
        return easeGUI$usesCustomTitleAnimation() ? 1.0F : original.call(value, min, max);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractWidget;setAlpha(F)V"))
    private void easeGUI$suppressVanillaWidgetFade(AbstractWidget widget, float alpha, Operation<Void> original) {
        if (!easeGUI$usesCustomTitleAnimation()) {
            original.call(widget, alpha);
        }
    }
}