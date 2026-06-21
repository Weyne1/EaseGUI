package net.weyne1.easegui.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.animation.AnimationProfile;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static net.weyne1.easegui.client.EaseGUIClient.LOGGER;

public class ConfigManager {
    private static final File CONFIG_FILE = new File(".", "config/easegui.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ModConfig currentConfig = new ModConfig();
    private static boolean isLoaded = false;

    private static Screen cachedScreenInstance = null;
    private static ScreenType cachedScreenType = ScreenRegistry.OTHER;

    public static void load() {
        if (isLoaded) return;

        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                currentConfig = GSON.fromJson(reader, ModConfig.class);

                if (currentConfig == null) {
                    currentConfig = new ModConfig();
                }

                if (currentConfig.mergeDefaults()) {
                    LOGGER.info("[EaseGUI] Config schema updated.");
                    save();
                }

                LOGGER.info("[EaseGUI] Config successfully loaded from disk.");
            } catch (Exception e) {
                LOGGER.error("[EaseGUI] Failed to read config, creating default... Error: {}", e.getMessage());
                currentConfig = new ModConfig();
                save();
            }
        } else {
            save();
        }

        isLoaded = true;
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(currentConfig, writer);
            LOGGER.info("[EaseGUI] Config successfully saved to disk.");
        } catch (IOException e) {
            LOGGER.error("[EaseGUI] Failed to save config: {}", e.getMessage());
        }
    }

    public static ModConfig getConfig() {
        if (!isLoaded) load();
        return currentConfig;
    }

    public static AnimationProfile getProfileForCurrentContext(UIElementCategory category) {
        if (category == null || category == UIElementCategory.UNKNOWN) return null;
        if (!isLoaded) load();

        Screen currentScreen = Minecraft.getInstance().screen;

        if (currentScreen != cachedScreenInstance) {
            cachedScreenInstance = currentScreen;
            cachedScreenType = ScreenRegistry.from(currentScreen);
        }

        ModConfig.ScreenSettings screenSettings = currentConfig.screens.get(cachedScreenType.getId());

        if (screenSettings != null) {
            if (!screenSettings.enabled) return null;

            AnimationProfile customProfile = screenSettings.customProfiles.get(category);
            if (customProfile != null) {
                return customProfile.enabled ? customProfile : null;
            }
        }

        return currentConfig.global.elementProfiles.get(category);
    }
}