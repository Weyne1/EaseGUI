package net.weyne1.easegui.neoforge.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.weyne1.easegui.client.EaseGUIClient;
import net.weyne1.easegui.client.gui.screens.EaseGUIMainConfigScreen;

@Mod(EaseGUIClient.MOD_ID)
public class EaseGUINeoForgeClient {

    public EaseGUINeoForgeClient(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> (container, parentScreen) -> new EaseGUIMainConfigScreen(parentScreen)
        );
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        EaseGUIClient.init();
    }
}