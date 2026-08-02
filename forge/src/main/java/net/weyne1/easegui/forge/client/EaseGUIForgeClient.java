package net.weyne1.easegui.forge.client;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.weyne1.easegui.client.EaseGUIClient;
import net.weyne1.easegui.client.gui.screens.MainConfigScreen;

@Mod(EaseGUIClient.MOD_ID)
public class EaseGUIForgeClient {

    public EaseGUIForgeClient(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::onClientSetup);

        context.getContainer().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parentScreen) -> new MainConfigScreen(parentScreen)
                )
        );
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        EaseGUIClient.init();
    }
}