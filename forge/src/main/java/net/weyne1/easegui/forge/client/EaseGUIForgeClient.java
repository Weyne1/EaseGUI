package net.weyne1.easegui.forge.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.weyne1.easegui.client.EaseGUIClient;
import net.weyne1.easegui.client.gui.screens.MainConfigScreen;

@Mod(EaseGUIClient.MOD_ID)
public class EaseGUIForgeClient {

    public EaseGUIForgeClient(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> (container, parentScreen) -> new MainConfigScreen(parentScreen)
        );
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        EaseGUIClient.init();
    }
}