package net.weyne1.easegui.client.gui.components;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.config.ConfigManager;

public abstract class EaseGUISlider extends AbstractSliderButton {
    private double initialValue;
    private boolean isEditingWithKeyboard = false;

    public EaseGUISlider(int x, int y, int width, int height, Component message, double initialValue) {
        super(x, y, width, height, message, initialValue);
        this.initialValue = this.value;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.initialValue = this.value;
        super.onClick(mouseX, mouseY);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        saveIfChanged();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean wasChanging = this.isEditingWithKeyboard;
        boolean handled = super.keyPressed(keyCode, scanCode, modifiers);

        if (CommonInputs.selected(keyCode)) {
            this.isEditingWithKeyboard = !this.isEditingWithKeyboard;

            if (wasChanging && !this.isEditingWithKeyboard) {
                saveIfChanged();
            }
        }

        return handled;
    }

    @Override
    public void setFocused(boolean focused) {
        if (!focused) {
            saveIfChanged();
        }
        super.setFocused(focused);
    }

    private void saveIfChanged() {
        if (Double.compare(this.initialValue, this.value) != 0) {
            ConfigManager.save();
            this.initialValue = this.value;
        }
    }
}