package net.weyne1.easegui.client.gui.components;

import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.config.EaseGUIConfig;

public class DimmingIntensitySlider extends EaseGUISlider {
    private final EaseGUIConfig config;

    public DimmingIntensitySlider(int x, int y, int width, int height, EaseGUIConfig config) {
        super(x, y, width, height, Component.empty(), config.global.backgroundDimmingIntensity);
        this.config = config;
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        int percent = (int) Math.round(this.value * 100.0);

        Component component = switch (percent) {
            case 100 -> Component.translatable("easegui.main.dimming_intensity_max");
            case 75 -> Component.translatable("easegui.main.dimming_intensity_vanilla");
            case 0 -> Component.translatable("easegui.main.dimming_intensity", Component.translatable("easegui.generic.off"));
            default -> Component.translatable("easegui.main.dimming_intensity", percent + "%");
        };

        this.setMessage(component);
    }

    @Override
    protected void applyValue() {
        config.global.backgroundDimmingIntensity = (float) (Math.round(this.value * 100.0) / 100.0);
    }
}