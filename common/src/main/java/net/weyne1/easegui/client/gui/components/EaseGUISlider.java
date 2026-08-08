package net.weyne1.easegui.client.gui.components;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.config.ConfigManager;
import org.jspecify.annotations.NonNull;

public abstract class EaseGUISlider extends AbstractSliderButton {
    private double initialValue;

    public EaseGUISlider(int x, int y, int width, int height, Component message, double initialValue) {
        super(x, y, width, height, message, initialValue);
        this.initialValue = this.value;
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        this.initialValue = this.value;
        super.onClick(event, doubleClick);
    }

    @Override
    public void onRelease(@NonNull MouseButtonEvent event) {
        super.onRelease(event);
        saveIfChanged();
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        boolean wasChanging = this.canChangeValue;
        boolean handled = super.keyPressed(event);

        if (event.isSelection() && wasChanging && !this.canChangeValue) {
            saveIfChanged();
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