package net.weyne1.easegui.client.extension;

import net.weyne1.easegui.client.config.EaseGUIElementCategory;

public interface EaseGUIWidgetExtension {
    EaseGUIElementCategory easeGUI$getCategory();

    float easeGUI$getAlpha();
}