package net.weyne1.easegui.client.gui.configurator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.gui.FieldValidator;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Registry of UI builders for different screens.
 */
public interface IScreenConfigurator {

    void populate(SettingsScrollList list, EaseGUIConfig.ScreenSettings settings, Screen parentScreen);

    default EditBox createLongField(Minecraft mc, String value, long min, long max, Consumer<Long> onSuccess) {
        EditBox editBox = new EditBox(mc.font, 0, 0, 60, 16, Component.empty());
        editBox.setValue(value);
        FieldValidator.registerLongValidator(editBox, min, max, onSuccess);
        return editBox;
    }

    default EditBox createFloatField(Minecraft mc, String value, float min, float max, Consumer<Float> onSuccess) {
        EditBox editBox = new EditBox(mc.font, 0, 0, 60, 16, Component.empty());
        editBox.setValue(value);
        FieldValidator.registerFloatValidator(editBox, min, max, onSuccess);
        return editBox;
    }

    default Component getOnOff(boolean state) {
        return Component.translatable(state ? "easegui.generic.on" : "easegui.generic.off");
    }

    Map<String, IScreenConfigurator> REGISTRY = new HashMap<>();

    static void init() {
        REGISTRY.put("title", new TitleScreenConfigurator());
        REGISTRY.put("advancements", new AdvancementsScreenConfigurator());
    }

    static IScreenConfigurator get(String screenId) {
        return REGISTRY.get(screenId);
    }
}