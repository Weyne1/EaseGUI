package net.weyne1.easegui.client.gui.preview;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public interface PreviewContentRenderer {
    void render(
            GuiGraphics graphics,
            Font font,
            int x,
            int y,
            int width,
            int height,
            int index,
            int alpha,
            boolean enabled
    );
}