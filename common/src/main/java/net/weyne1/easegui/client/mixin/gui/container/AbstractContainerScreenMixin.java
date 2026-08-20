package net.weyne1.easegui.client.mixin.gui.container;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.extension.ContainerScreenExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen implements ContainerScreenExtension {

    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Final @Shadow protected int imageWidth;
    @Final @Shadow protected int imageHeight;

    protected AbstractContainerScreenMixin(Component title) { super(title); }

    @Override public int easegui$getLeftPos() { return leftPos; }
    @Override public int easegui$getTopPos() { return topPos; }
    @Override public int easegui$getImageWidth() { return imageWidth; }
    @Override public int easegui$getImageHeight() { return imageHeight; }
}