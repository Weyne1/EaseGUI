package net.weyne1.easegui.client.mixin.accessor;

import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementTab.class)
public interface AdvancementTabAccessor {
    @Accessor("index")
    int easeGUI$getIndex();
}