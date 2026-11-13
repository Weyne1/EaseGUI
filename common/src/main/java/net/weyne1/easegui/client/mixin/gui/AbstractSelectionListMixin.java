package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.WidgetAnimationState;
import net.weyne1.easegui.client.animator.ListItemAnimator;
import net.weyne1.easegui.client.animator.ScrollableListAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractSelectionList.class)
public class AbstractSelectionListMixin {

    @Unique
    private final WidgetAnimationState easegui$scrollAnimationState = new WidgetAnimationState();
    @Shadow protected int width;
    @Shadow protected int height;
    @Shadow protected int x0;
    @Shadow protected int y0;

    @WrapMethod(method = "render")
    private void easegui$wrapListRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        try (AnimationScope ignored = ScrollableListAnimator.beginRender(graphics, this.x0, this.y0, this.width, this.height, this.easegui$scrollAnimationState)) {
            original.call(graphics, mouseX, mouseY, partialTick);
        }
    }

    @WrapMethod(method = "renderItem")
    private void easegui$wrapRenderItem(GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                                        int index, int left, int top, int width, int height, Operation<Void> original) {
        try (AnimationScope ignored = ListItemAnimator.beginRender(
                graphics, top, left, width, height
        )) {
            AnimationContext.pushParentAnimation();
            try {
                original.call(graphics, mouseX, mouseY, partialTick, index, left, top, width, height);
            } finally {
                AnimationContext.popParentAnimation();
            }
        }
    }
}