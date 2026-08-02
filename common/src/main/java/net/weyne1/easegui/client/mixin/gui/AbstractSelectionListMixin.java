package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationState;
import net.weyne1.easegui.client.animator.ListItemAnimator;
import net.weyne1.easegui.client.animator.ScrollableListAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractSelectionList.class)
public class AbstractSelectionListMixin {

    @Unique private final AnimationState easeGUI$scrollAnimationState = new AnimationState();
    @Shadow protected int width;
    @Shadow protected int height;
    @Shadow protected int x0;
    @Shadow protected int y0;

    @WrapMethod(method = "render")
    private void easeGUI$wrapListRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        try (AnimationScope ignored = ScrollableListAnimator.beginRender(guiGraphics, this.x0, this.y0, this.width, this.height, this.easeGUI$scrollAnimationState)) {
            original.call(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @WrapMethod(method = "renderItem")
    private void easeGUI$wrapRenderItem(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
                                        int index, int left, int top, int width, int height, Operation<Void> original) {
        try (AnimationScope ignored = ListItemAnimator.beginRender(
                guiGraphics, top, left, width, height
        )) {
            AnimationContext.pushParentAnimation();
            try {
                original.call(guiGraphics, mouseX, mouseY, partialTick, index, left, top, width, height);
            } finally {
                AnimationContext.popParentAnimation();
            }
        }
    }
}