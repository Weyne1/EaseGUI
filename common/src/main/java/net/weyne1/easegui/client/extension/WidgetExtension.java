package net.weyne1.easegui.client.extension;

import net.weyne1.easegui.api.WidgetCategory;

public interface WidgetExtension {
    WidgetCategory easeGUI$getCategory();

    void easeGUI$setExcluded(boolean excluded);
    boolean easeGUI$isExcluded();
}