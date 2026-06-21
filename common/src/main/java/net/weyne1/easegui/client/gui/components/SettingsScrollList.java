package net.weyne1.easegui.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingsScrollList extends ContainerObjectSelectionList<SettingsScrollList.Entry> {

    private static final int SCROLLBAR_WIDTH_GAP = 14;
    private static final int ELEMENT_SPACING = 4;
    private static final int WIDGET_HEIGHT = 20;
    private static final float LABEL_WIDTH_RATIO = 0.55f;
    private static final int COLOR_HEADER = 0xFFAAAAAA;

    public SettingsScrollList(Minecraft mc, int width, int height, int top, int itemHeight) {
        super(mc, width, height, top, itemHeight);
    }

    @Override
    public int getRowWidth() {
        return this.width - 40;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getX() + this.width - 6;
    }

    public void addHeader(String text) { this.addEntry(new HeaderEntry(text)); }

    public void addButton(Button btn) { this.addEntry(new ButtonEntry(btn)); }
    public void addTwoButtons(Button btn1, Button btn2) {this.addTwoButtons(btn1, btn2, 0.60f); }
    public void addTwoButtons(Button btn1, Button btn2, float firstButtonRatio) { this.addEntry(new TwoButtonsEntry(this.getRowWidth(), btn1, btn2, firstButtonRatio)); }

    public void addField(String label, EditBox box) { this.addEntry(new FieldEntry(this.getRowWidth(), label, box)); }
    public void addTwoFields(String label, EditBox box1, EditBox box2) { this.addEntry(new TwoFieldsEntry(this.getRowWidth(), label, box1, box2)); }


    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> { }

    public static class HeaderEntry extends Entry {
        private final Component text;

        public HeaderEntry(String text) {
            this.text = Component.literal(text);
        }

        @Override
        public void render(GuiGraphics gg, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean isHovered, float partialTick) {
            Font font = Minecraft.getInstance().font;
            gg.drawCenteredString(font, this.text, left + width / 2, top + (height - 9) / 2, COLOR_HEADER);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() { return List.of(); }
        @Override
        public @NotNull List<? extends NarratableEntry> narratables() { return List.of(); }
    }

    public static class ButtonEntry extends Entry {
        private final Button button;

        public ButtonEntry(Button button) {
            this.button = button;
        }

        @Override
        public void render(GuiGraphics gg, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean isHovered, float partialTick) {
            button.setWidth(width - SCROLLBAR_WIDTH_GAP);
            button.setX(left);
            button.setY(top);
            button.render(gg, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() { return List.of(button); }
        @Override
        public @NotNull List<? extends NarratableEntry> narratables() { return List.of(button); }
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
        public void render(GuiGraphics gg, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean isHovered, float partialTick) {
            button1.setX(left);
            button1.setY(top);

            button2.setX(left + button1.getWidth() + ELEMENT_SPACING);
            button2.setY(top);

            button1.render(gg, mouseX, mouseY, partialTick);
            button2.render(gg, mouseX, mouseY, partialTick);
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
        public void render(GuiGraphics gg, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean isHovered, float partialTick) {
            label.setX(left);
            label.setY(top);
            field.setX(left + label.getWidth() + ELEMENT_SPACING);
            field.setY(top);

            label.render(gg, mouseX, mouseY, partialTick);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            field.render(gg, mouseX, mouseY, partialTick);
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
        public void render(GuiGraphics gg, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean isHovered, float partialTick) {
            label.setX(left);
            label.setY(top);

            field1.setX(left + label.getWidth() + ELEMENT_SPACING);
            field1.setY(top);

            field2.setX(field1.getX() + field1.getWidth() + ELEMENT_SPACING);
            field2.setY(top);

            label.render(gg, mouseX, mouseY, partialTick);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            field1.render(gg, mouseX, mouseY, partialTick);
            field2.render(gg, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() { return List.of(label, field1, field2); }
        @Override
        public @NotNull List<? extends NarratableEntry> narratables() { return List.of(label, field1, field2); }
    }
}