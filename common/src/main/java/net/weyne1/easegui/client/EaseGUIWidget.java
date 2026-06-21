package net.weyne1.easegui.client;

import net.weyne1.easegui.client.config.UIElementCategory;

public interface EaseGUIWidget {
    UIElementCategory easeGUI$getCategory();

    float easeGUI$getAlpha();
}