package net.weyne1.easegui.client.gui.preview;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

final class AbstractElementPreview implements PreviewContentRenderer {
    private static final Component STATIC_LABEL = Component.translatable("easegui.editor.preview.element");

    private static final Component[] CASCADE_LABELS = {
            Component.translatable("easegui.editor.preview.element_idx", 1),
            Component.translatable("easegui.editor.preview.element_idx", 2),
            Component.translatable("easegui.editor.preview.element_idx", 3)
    };

    private static final Component[] CASCADE_SHORT_LABELS = {
            Component.literal("#1"),
            Component.literal("#2"),
            Component.literal("#3")
    };

    private final boolean isCascade;
    private final boolean isHorizontal;

    AbstractElementPreview(boolean isCascade, boolean isHorizontal) {
        this.isCascade = isCascade;
        this.isHorizontal = isHorizontal;
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
        int boxColor = enabled ? 0x353535 : 0x222222;
        graphics.fill(x, y, x + width, y + height, (alpha << 24) | boxColor);

        Component label = isCascade
                ? (isHorizontal ? CASCADE_SHORT_LABELS[index] : CASCADE_LABELS[index])
                : STATIC_LABEL;

        int textColor = enabled ? 0xE0E0E0 : 0x888888;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        graphics.centeredText(font, label, centerX, centerY - 4, (alpha << 24) | textColor);
    }
}