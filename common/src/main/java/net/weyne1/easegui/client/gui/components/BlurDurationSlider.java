package net.weyne1.easegui.client.gui.components;

import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.config.EaseGUIConfig;

public class BlurDurationSlider extends EaseGUISlider {
    private final EaseGUIConfig config;

    public BlurDurationSlider(int x, int y, int width, int height, EaseGUIConfig config) {
        super(x, y, width, height, Component.empty(), calculateInitialValue(config));
        this.config = config;
        this.updateMessage();
    }

    private static double calculateInitialValue(EaseGUIConfig config) {
        if (config.global.blurDuration <= 0) {
            return 0.0;
        }
        int steps = Math.round((float) config.global.blurDuration / 100.0f);
        return Math.clamp(steps / 10.0, 0.1, 1.0);
    }

    @Override
    protected void updateMessage() {
        int steps = (int) Math.round(this.value * 10.0);

        if (steps == 0) {
            this.setMessage(Component.translatable("easegui.main.smooth_blur", Component.translatable("easegui.generic.off")));
        } else {
            int ms = steps * 100;
            this.setMessage(Component.translatable("easegui.main.smooth_blur_ms", ms));
        }
    }

    @Override
    protected void applyValue() {
        int steps = (int) Math.round(this.value * 10.0);
        config.global.blurDuration = steps * 100L;
    }
}