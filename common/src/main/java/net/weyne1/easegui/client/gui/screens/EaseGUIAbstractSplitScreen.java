package net.weyne1.easegui.client.gui.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public abstract class EaseGUIAbstractSplitScreen extends Screen {
    protected final Screen parent;
    protected int halfWidth;
    protected int listWidth;
    protected int listHeight;
    protected int leftX;
    protected int rightX;

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

        initScreen();

        // Главный заголовок экрана (в самом верху по центру)
        Component titleColored = this.title.copy().withStyle(ChatFormatting.WHITE);
        StringWidget titleWidget = new StringWidget(titleColored, this.font);

        titleWidget.setX(this.halfWidth - titleWidget.getWidth() / 2);
        titleWidget.setY(15);
        this.addRenderableWidget(titleWidget);

        // Левый подзаголовок
        Component leftSub = getLeftSubtitle();
        if (leftSub != null) {
            Component leftSubColored = leftSub.copy().withStyle(ChatFormatting.GRAY);
            StringWidget leftSubWidget = new StringWidget(leftSubColored, this.font);

            leftSubWidget.setX((this.halfWidth / 2) - (leftSubWidget.getWidth() / 2));
            leftSubWidget.setY(35);
            this.addRenderableWidget(leftSubWidget);
        }

        // Правый подзаголовок
        Component rightSub = getRightSubtitle();
        if (rightSub != null) {
            Component rightSubColored = rightSub.copy().withStyle(ChatFormatting.GRAY);
            StringWidget rightSubWidget = new StringWidget(rightSubColored, this.font);

            rightSubWidget.setX((this.halfWidth + (this.halfWidth / 2)) - (rightSubWidget.getWidth() / 2));
            rightSubWidget.setY(35);
            this.addRenderableWidget(rightSubWidget);
        }
    }

    protected abstract void initScreen();

    protected abstract Component getLeftSubtitle();

    protected abstract Component getRightSubtitle();

    protected void renderOverlay(GuiGraphicsExtractor gg, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        // Вертикальный разделитель по центру экрана
        graphics.fill(halfWidth - 1, 50, halfWidth + 1, this.height - 45, LINE_COLOR);

        renderOverlay(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(this.parent);
    }
}