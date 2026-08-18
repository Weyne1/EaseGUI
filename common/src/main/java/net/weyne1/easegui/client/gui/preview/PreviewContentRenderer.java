package net.weyne1.easegui.client.gui.preview;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface PreviewContentRenderer {

    void extractContent(
            GuiGraphicsExtractor graphics,
            Font font,
            int x,
            int y,
            int width,
            int height,
            int index,
            int alpha,
            boolean enabled
    );

    default int getPreferredWidth(boolean isCascade, boolean isHorizontal) {
        return (isCascade && isHorizontal) ? 40 : 120;
    }

    default int getPreferredHeight() {
        return 24;
    }
}