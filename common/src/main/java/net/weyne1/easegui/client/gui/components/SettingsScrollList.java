package net.weyne1.easegui.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.weyne1.easegui.api.animation.EasingType;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.IntConsumer;

public class SettingsScrollList extends ContainerObjectSelectionList<SettingsScrollList.Entry> {

    private static final int SCROLLBAR_WIDTH_GAP = 14;
    private static final int ELEMENT_SPACING = 4;
    private static final int WIDGET_HEIGHT = 20;
    private static final float LABEL_WIDTH_RATIO = 0.55f;
    private static final int MAX_ROW_WIDTH = 400;
    private IntConsumer scrollListener;

    public SettingsScrollList(Minecraft mc, int width, int height, int top, int itemHeight) {
        super(mc, width, height, top, itemHeight);
    }

    public void setScrollListener(IntConsumer listener) {
        this.scrollListener = listener;
    }

    @Override
    public void setScrollAmount(double amount) {
        super.setScrollAmount(amount);
        if (this.scrollListener != null) {
            this.scrollListener.accept((int) amount);
        }
    }

    @Override
    public int getRowWidth() {
        return Math.min(this.width - 40, MAX_ROW_WIDTH);
    }

    public void addHeader(String text) {
        this.addEntry(new HeaderEntry(text));
    }

    public void addWidget(AbstractWidget widget) {
        this.addEntry(new WidgetEntry(this.getRowWidth(), widget));
    }

    public void addTwoButtons(Button btn1, Button btn2) {
        this.addTwoButtons(btn1, btn2, 0.75f);
    }

    public void addTwoButtons(Button btn1, Button btn2, float firstButtonRatio) {
        this.addEntry(new TwoButtonsEntry(this.getRowWidth(), btn1, btn2, firstButtonRatio));
    }

    public void addField(String label, EditBox box) {
        this.addEntry(new FieldEntry(this.getRowWidth(), label, box));
    }

    public void addTwoFields(String label, EditBox box1, EditBox box2) {
        this.addEntry(new TwoFieldsEntry(this.getRowWidth(), label, box1, box2));
    }

    public void addLabelAndButton(String label, Button button) {
        this.addLabelAndButton(label, button, 0.25f);
    }

    public void addLabelAndButton(String label, Button button, float buttonRatio) {
        this.addEntry(new LabelAndButtonEntry(this.getRowWidth(), label, button, buttonRatio));
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> { }

    public static class HeaderEntry extends Entry {
        private final Component text;
        private static final int HEADER_COLOR = 0xFFAAAAAA;
        private static final int HEADER_PADDING = 6;
        private static final int HEADER_HEIGHT = 24;

        public HeaderEntry(String text) {
            this.text = Component.literal(text);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            Font font = Minecraft.getInstance().font;
            int startY = this.getContentY() + HEADER_HEIGHT - font.lineHeight - HEADER_PADDING;
            String textStr = this.text.getString();
            graphics.centeredText(font, Component.literal(textStr), this.getContentXMiddle(), startY, HEADER_COLOR);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() { return List.of(); }
        @Override
        public @NotNull List<? extends NarratableEntry> narratables() { return List.of(); }
    }

    public static class WidgetEntry extends Entry {
        private final AbstractWidget widget;

        public WidgetEntry(int listWidth, AbstractWidget widget) {
            this.widget = widget;
            this.widget.setWidth(listWidth - SCROLLBAR_WIDTH_GAP);
            this.widget.setHeight(WIDGET_HEIGHT);
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            widget.setX(this.getContentX() + SCROLLBAR_WIDTH_GAP / 2);
            widget.setY(this.getContentY());
            widget.extractRenderState(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() { return List.of(widget); }
        @Override
        public @NotNull List<? extends NarratableEntry> narratables() { return List.of(widget); }
    }

    public static class TwoButtonsEntry extends Entry {
        private final Button button1;
        private final Button button2;

        public TwoButtonsEntry(int listWidth, Button button1, Button button2, float ratio1) {
            int availWidth = listWidth - SCROLLBAR_WIDTH_GAP;
            int totalWidgetsWidth = availWidth - ELEMENT_SPACING;

            int btn1W = (int) (totalWidgetsWidth * ratio1);
            int btn2W = totalWidgetsWidth - btn1W;

            this.button1 = button1;
            this.button1.setWidth(btn1W);
            this.button1.setHeight(WIDGET_HEIGHT);

            this.button2 = button2;
            this.button2.setWidth(btn2W);
            this.button2.setHeight(WIDGET_HEIGHT);
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            button1.setX(this.getContentX() + SCROLLBAR_WIDTH_GAP / 2);
            button1.setY(this.getContentY());

            button2.setX(button1.getX() + button1.getWidth() + ELEMENT_SPACING);
            button2.setY(this.getContentY());

            button1.extractRenderState(graphics, mouseX, mouseY, partialTick);
            button2.extractRenderState(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() { return List.of(button1, button2); }
        @Override
        public @NotNull List<? extends NarratableEntry> narratables() { return List.of(button1, button2); }
    }

    public static class FieldEntry extends Entry {
        private final Button label;
        private final EditBox field;

        public FieldEntry(int listWidth, String labelText, EditBox field) {
            int availWidth = listWidth - SCROLLBAR_WIDTH_GAP;
            int lblW = (int) (availWidth * LABEL_WIDTH_RATIO);
            int fldW = availWidth - lblW - ELEMENT_SPACING;

            this.label = Button.builder(Component.literal(labelText), _ -> {})
                    .bounds(0, 0, lblW, WIDGET_HEIGHT)
                    .build();
            this.label.active = false;

            this.field = field;
            this.field.setWidth(fldW);
            this.field.setHeight(WIDGET_HEIGHT);
            this.field.setValue(this.field.getValue());
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            label.setX(this.getContentX() + SCROLLBAR_WIDTH_GAP / 2);
            label.setY(this.getContentY());
            field.setX(label.getX() + label.getWidth() + ELEMENT_SPACING);
            field.setY(this.getContentY());

            label.extractRenderState(graphics, mouseX, mouseY, partialTick);
            field.extractRenderState(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() { return List.of(label, field); }
        @Override
        public @NotNull List<? extends NarratableEntry> narratables() { return List.of(label, field); }
    }

    public static class TwoFieldsEntry extends Entry {
        private final Button label;
        private final EditBox field1;
        private final EditBox field2;

        public TwoFieldsEntry(int listWidth, String labelText, EditBox field1, EditBox field2) {
            int availWidth = listWidth - SCROLLBAR_WIDTH_GAP;
            int lblW = (int) (availWidth * LABEL_WIDTH_RATIO);
            int fieldsArea = availWidth - lblW - ELEMENT_SPACING;
            int subFldW = (fieldsArea - ELEMENT_SPACING) / 2;

            this.label = Button.builder(Component.literal(labelText), _ -> {})
                    .bounds(0, 0, lblW, WIDGET_HEIGHT)
                    .build();
            this.label.active = false;

            this.field1 = field1;
            this.field1.setWidth(subFldW);
            this.field1.setHeight(WIDGET_HEIGHT);
            this.field1.setValue(this.field1.getValue());

            this.field2 = field2;
            this.field2.setWidth(fieldsArea - subFldW - ELEMENT_SPACING);
            this.field2.setHeight(WIDGET_HEIGHT);
            this.field2.setValue(this.field2.getValue());
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            label.setX(this.getContentX() + SCROLLBAR_WIDTH_GAP / 2);
            label.setY(this.getContentY());

            field1.setX(label.getX() + label.getWidth() + ELEMENT_SPACING);
            field1.setY(this.getContentY());

            field2.setX(field1.getX() + field1.getWidth() + ELEMENT_SPACING);
            field2.setY(this.getContentY());

            label.extractRenderState(graphics, mouseX, mouseY, partialTick);
            field1.extractRenderState(graphics, mouseX, mouseY, partialTick);
            field2.extractRenderState(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() { return List.of(label, field1, field2); }
        @Override
        public @NotNull List<? extends NarratableEntry> narratables() { return List.of(label, field1, field2); }
    }

    public static class LabelAndButtonEntry extends Entry {
        private static final int LABEL_PADDING = 4;
        private static final int LABEL_GAP = 4;
        private static final Component ELLIPSIS = Component.literal("…");

        private final StringWidget labelWidget;
        private final Button button;
        private final int availWidth;
        private final int buttonWidth;
        private boolean isHovering = false;
        private long animationStartTime = 0L;

        public LabelAndButtonEntry(int listWidth, String labelText, Button button, float buttonRatio) {
            this.availWidth = listWidth - SCROLLBAR_WIDTH_GAP;
            this.buttonWidth = (int) (availWidth * buttonRatio);
            int labelAreaWidth = availWidth - buttonWidth;

            this.button = button;
            this.button.setWidth(buttonWidth);
            this.button.setHeight(WIDGET_HEIGHT);

            Font font = Minecraft.getInstance().font;
            Component fullText = Component.literal(labelText);
            int maxLabelWidth = Math.max(0, labelAreaWidth - LABEL_PADDING - LABEL_GAP);

            Component displayText;
            boolean truncated = font.width(fullText) > maxLabelWidth;
            if (truncated) {
                int ellipsisWidth = font.width(ELLIPSIS);
                String cut = font.plainSubstrByWidth(labelText, Math.max(0, maxLabelWidth - ellipsisWidth));
                displayText = Component.literal(cut).append(ELLIPSIS);
            } else {
                displayText = fullText;
            }

            this.labelWidget = new StringWidget(displayText, font);
            this.labelWidget.setWidth(labelAreaWidth - LABEL_PADDING);
            this.labelWidget.setHeight(WIDGET_HEIGHT);

            if (truncated) {
                this.labelWidget.setTooltip(Tooltip.create(fullText));
            }
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            long now = Util.getMillis();

            final float TARGET_ALPHA = 0.15f;
            final long DURATION_MS = 200L;

            if (isHovered && !this.isHovering) {
                this.isHovering = true;
                this.animationStartTime = now;
            } else if (!isHovered && this.isHovering) {
                this.isHovering = false;
                this.animationStartTime = now;
            }

            int startX = this.getContentX() + SCROLLBAR_WIDTH_GAP / 2;
            int startY = this.getContentY();

            long elapsed = now - this.animationStartTime;
            boolean isAnimating = this.isHovering || (elapsed < DURATION_MS);

            if (isAnimating) {
                float linearProgress = Math.min(1.0f, elapsed / (float) DURATION_MS);
                float effectiveProgress = this.isHovering ? linearProgress : (1.0f - linearProgress);
                float currentAlpha = EasingType.EASE_OUT_CUBIC.ease(effectiveProgress) * TARGET_ALPHA;

                try (AnimationScope _ = AnimationSystem.beginAlphaOnly(graphics, currentAlpha)) {
                    graphics.fill(startX, startY, startX + availWidth, startY + WIDGET_HEIGHT, 0xFFFFFFFF);
                }
            }

            labelWidget.setX(startX + LABEL_PADDING);
            labelWidget.setY(startY);
            labelWidget.extractRenderState(graphics, mouseX, mouseY, partialTick);

            int buttonX = startX + availWidth - buttonWidth;
            button.setX(buttonX);
            button.setY(startY);
            button.extractRenderState(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(labelWidget, button);
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(labelWidget, button);
        }
    }
}