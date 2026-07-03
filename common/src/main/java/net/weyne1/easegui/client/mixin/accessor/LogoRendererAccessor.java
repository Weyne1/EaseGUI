package net.weyne1.easegui.client.mixin.accessor;

import net.minecraft.client.gui.components.LogoRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LogoRenderer.class)
public interface LogoRendererAccessor {

    @Accessor("LOGO_WIDTH")
    static int easeGUI$getLogoWidth() { throw new AssertionError(); }

    @Accessor("LOGO_HEIGHT")
    static int easeGUI$getLogoHeight() { throw new AssertionError(); }

    @Accessor("LOGO_TEXTURE_HEIGHT")
    static int easeGUI$getLogoTextureHeight() { throw new AssertionError(); }

    @Accessor("EDITION_WIDTH")
    static int easeGUI$getEditionWidth() { throw new AssertionError(); }

    @Accessor("EDITION_HEIGHT")
    static int easeGUI$getEditionHeight() { throw new AssertionError(); }

    @Accessor("EDITION_TEXTURE_HEIGHT")
    static int easeGUI$getEditionTextureHeight() { throw new AssertionError(); }
}