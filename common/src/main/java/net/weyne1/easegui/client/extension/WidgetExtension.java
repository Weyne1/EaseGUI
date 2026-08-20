package net.weyne1.easegui.client.extension;

import net.weyne1.easegui.api.WidgetCategory;

public interface WidgetExtension {
    WidgetCategory easegui$getCategory();

    void easegui$setExcluded(boolean excluded);
    boolean easegui$isExcluded();
}