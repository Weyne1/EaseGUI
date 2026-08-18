package net.weyne1.easegui.client.gui.preview;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class AbstractContainerPreview implements PreviewContentRenderer {
    private static final Identifier CONTAINER_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/container/shulker_box.png");
    private static final float SCALE_FACTOR = 0.8F;

    @Override
    public int getPreferredWidth(boolean isCascade, boolean isHorizontal) {
        return (int) (176 * SCALE_FACTOR);
    }

    @Override
    public int getPreferredHeight() {
        return (int) (166 * SCALE_FACTOR);
    }

    @Override
    public void extractContent(
            GuiGraphicsExtractor graphics,
            Font font,
            int x,
            int y,
            int width,
            int height,
            int index,
            int alpha,
            boolean enabled
    ) {
        int color = enabled ? 0xFFFFFFFF : 0xFF555555;
        int size = (int) (256 * SCALE_FACTOR);

        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, x, y, 0.0F, 0.0F, width, height, size, size, color);
    }
}