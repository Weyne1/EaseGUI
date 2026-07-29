package net.weyne1.easegui.client.extension;

import net.weyne1.easegui.client.config.UIElementCategory;

public interface WidgetExtension {
    UIElementCategory easeGUI$getCategory();

    float easeGUI$getAlpha();
}