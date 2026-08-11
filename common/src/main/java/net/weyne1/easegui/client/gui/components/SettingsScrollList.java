package net.weyne1.easegui.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.Util;
import net.weyne1.easegui.api.animation.EasingType;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationSystem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingsScrollList extends ContainerObjectSelectionList<SettingsScrollList.Entry> {

    private static final int SCROLLBAR_WIDTH_GAP = 14;
    private static final int ELEMENT_SPACING = 4;
    private static final int WIDGET_HEIGHT = 20;
    private static final float LABEL_WIDTH_RATIO = 0.55f;
    private static final int MAX_ROW_WIDTH = 350;

    public SettingsScrollList(Minecraft mc, int width, int height, int top, int itemHeight) {
        super(mc, width, height, top, itemHeight);
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
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean isHovered, float partialTick) {
            Font font = Minecraft.getInstance().font;
            int startY = top + HEADER_HEIGHT - font.lineHeight - HEADER_PADDING;
            String textStr = this.text.getString();
            graphics.drawCenteredString(font, Component.literal(textStr), left + width / 2, startY, HEADER_COLOR);
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
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean isHovered, float partialTick) {
            widget.setWidth(width - SCROLLBAR_WIDTH_GAP);
            widget.setX(left + (SCROLLBAR_WIDTH_GAP / 2));
            widget.setY(top);
            widget.render(graphics, mouseX, mouseY, partialTick);
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
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean isHovered, float partialTick) {
            button1.setX(left + (SCROLLBAR_WIDTH_GAP / 2));
            button1.setY(top);

            button2.setX(button1.getX() + button1.getWidth() + ELEMENT_SPACING);
            button2.setY(top);

            button1.render(graphics, mouseX, mouseY, partialTick);
            button2.render(graphics, mouseX, mouseY, partialTick);
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

            this.label = Button.builder(Component.literal(labelText), b -> {})
                    .bounds(0, 0, lblW, WIDGET_HEIGHT)
                    .build();
            this.label.active = false;

            this.field = field;
            this.field.setWidth(fldW);
            this.field.setHeight(WIDGET_HEIGHT);
            this.field.setValue(this.field.getValue());
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean isHovered, float partialTick) {
            label.setX(left + (SCROLLBAR_WIDTH_GAP / 2));
            label.setY(top);
            field.setX(label.getX() + label.getWidth() + ELEMENT_SPACING);
            field.setY(top);

            label.render(graphics, mouseX, mouseY, partialTick);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            field.render(graphics, mouseX, mouseY, partialTick);
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

            this.label = Button.builder(Component.literal(labelText), b -> {})
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
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean isHovered, float partialTick) {
            label.setX(left + (SCROLLBAR_WIDTH_GAP / 2));
            label.setY(top);

            field1.setX(label.getX() + label.getWidth() + ELEMENT_SPACING);
            field1.setY(top);

            field2.setX(field1.getX() + field1.getWidth() + ELEMENT_SPACING);
            field2.setY(top);

            label.render(graphics, mouseX, mouseY, partialTick);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            field1.render(graphics, mouseX, mouseY, partialTick);
            field2.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() { return List.of(label, field1, field2); }
        @Override
        public @NotNull List<? extends NarratableEntry> narratables() { return List.of(label, field1, field2); }
    }

    public static class LabelAndButtonEntry extends Entry {
        private static final int LABEL_PADDING = 4;
        private final Component label;
        private final Button button;
        private final int availWidth;
        private final int buttonWidth;
        private boolean isHovering = false;
        private long animationStartTime = 0L;

        public LabelAndButtonEntry(int listWidth, String labelText, Button button, float buttonRatio) {
            this.availWidth = listWidth - SCROLLBAR_WIDTH_GAP;
            this.buttonWidth = (int)(availWidth * buttonRatio);

            this.label = Component.literal(labelText);
            this.button = button;
            this.button.setWidth(buttonWidth);
            this.button.setHeight(WIDGET_HEIGHT);
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            Font font = Minecraft.getInstance().font;
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

            int startX = left + SCROLLBAR_WIDTH_GAP / 2;

            long elapsed = now - this.animationStartTime;
            boolean isAnimating = this.isHovering || (elapsed < DURATION_MS);

            if (isAnimating) {
                float linearProgress = Math.min(1.0f, elapsed / (float) DURATION_MS);
                float effectiveProgress = this.isHovering ? linearProgress : (1.0f - linearProgress);
                float currentAlpha = EasingType.EASE_OUT_CUBIC.ease(effectiveProgress) * TARGET_ALPHA;

                try (AnimationScope ignored = AnimationSystem.beginAlphaOnly(graphics, currentAlpha)) {
                    graphics.fill(startX, top, startX + availWidth, top + WIDGET_HEIGHT, 0xFFFFFFFF);
                }
            }

            int labelX = startX + LABEL_PADDING;
            int labelY = top + (WIDGET_HEIGHT - font.lineHeight) / 2;
            graphics.drawString(font, this.label, labelX, labelY, 0xFFFFFFFF);

            int buttonX = startX + availWidth - buttonWidth;
            button.setX(buttonX);
            button.setY(top);
            button.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(button);
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(button);
        }
    }
}