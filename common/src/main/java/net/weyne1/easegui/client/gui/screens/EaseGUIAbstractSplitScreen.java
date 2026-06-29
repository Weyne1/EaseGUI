package net.weyne1.easegui.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class EaseGUIAbstractSplitScreen extends Screen {
    protected final Screen parent;
    protected int halfWidth;
    protected int listWidth;
    protected int listHeight;
    protected int leftX;
    protected int rightX;
    protected int stringColor;

    private static final int LINE_COLOR = 0x33FFFFFF;

    public EaseGUIAbstractSplitScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.halfWidth = this.width / 2;

        this.listWidth = halfWidth;
        this.listHeight = this.height - 50 - 45;

        this.leftX = 0;
        this.rightX = halfWidth;

        this.stringColor = 0xAAAAAA;

        initScreen();

        // Главный заголовок экрана (в самом верху по центру)
        StringWidget titleWidget = new StringWidget(this.title, this.font);
        titleWidget.setX(this.halfWidth - titleWidget.getWidth() / 2);
        titleWidget.setY(15);
        titleWidget.setColor(0xFFFFFF);
        this.addRenderableWidget(titleWidget);

        // Левый подзаголовок
        Component leftSub = getLeftSubtitle();
        if (leftSub != null) {
            StringWidget leftSubWidget = new StringWidget(leftSub, this.font);
            leftSubWidget.setX((this.halfWidth / 2) - (leftSubWidget.getWidth() / 2));
            leftSubWidget.setY(35);
            leftSubWidget.setColor(this.stringColor);
            this.addRenderableWidget(leftSubWidget);
        }

        // Правый подзаголовок
        Component rightSub = getRightSubtitle();
        if (rightSub != null) {
            StringWidget rightSubWidget = new StringWidget(rightSub, this.font);
            rightSubWidget.setX((this.halfWidth + (this.halfWidth / 2)) - (rightSubWidget.getWidth() / 2));
            rightSubWidget.setY(35);
            rightSubWidget.setColor(this.stringColor);
            this.addRenderableWidget(rightSubWidget);
        }
    }

    protected abstract void initScreen();

    protected abstract Component getLeftSubtitle();

    protected abstract Component getRightSubtitle();

    protected void renderOverlay(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.render(gg, mouseX, mouseY, partialTick);

        // Вертикальный разделитель по центру экрана
        gg.fill(halfWidth - 1, 50, halfWidth + 1, this.height - 45, LINE_COLOR);

        renderOverlay(gg, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }
}