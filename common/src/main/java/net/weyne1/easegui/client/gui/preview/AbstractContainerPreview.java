package net.weyne1.easegui.client.gui.preview;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class AbstractContainerPreview implements PreviewContentRenderer {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/shulker_box.png");
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
    public void render(
            GuiGraphics graphics,
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

        float a = (color >>> 24) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        RenderSystem.setShaderColor(r, g, b, a);
        try {
            graphics.blit(CONTAINER_BACKGROUND, x, y, 0.0F, 0.0F, width, height, size, size);
        } finally {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}