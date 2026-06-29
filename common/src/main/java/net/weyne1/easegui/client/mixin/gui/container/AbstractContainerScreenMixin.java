package net.weyne1.easegui.client.mixin.gui.container;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.accessor.ContainerScreenAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen implements ContainerScreenAccessor {

    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected int imageWidth;
    @Shadow protected int imageHeight;

    protected AbstractContainerScreenMixin(Component title) { super(title); }

    @Override public int easeGUI$getLeftPos() { return leftPos; }
    @Override public int easeGUI$getTopPos() { return topPos; }
    @Override public int easeGUI$getImageWidth() { return imageWidth; }
    @Override public int easeGUI$getImageHeight() { return imageHeight; }
}