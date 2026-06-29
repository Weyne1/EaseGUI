package net.weyne1.easegui.client;

import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.gui.configurator.IScreenConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EaseGUIClient {
    public static final String MOD_ID = "easegui";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("[EaseGUI] Initializing EaseGUI...");

        ConfigManager.load();
        IScreenConfigurator.init();
    }
}