package net.weyne1.easegui.client.mixin.accessor;

import net.minecraft.client.gui.components.LogoRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LogoRenderer.class)
public interface LogoRendererAccessor {

    @Accessor("LOGO_WIDTH")
    static int easegui$getLogoWidth() { throw new AssertionError(); }

    @Accessor("LOGO_HEIGHT")
    static int easegui$getLogoHeight() { throw new AssertionError(); }

    @Accessor("LOGO_TEXTURE_HEIGHT")
    static int easegui$getLogoTextureHeight() { throw new AssertionError(); }

    @Accessor("EDITION_WIDTH")
    static int easegui$getEditionWidth() { throw new AssertionError(); }

    @Accessor("EDITION_HEIGHT")
    static int easegui$getEditionHeight() { throw new AssertionError(); }

    @Accessor("EDITION_TEXTURE_HEIGHT")
    static int easegui$getEditionTextureHeight() { throw new AssertionError(); }
}