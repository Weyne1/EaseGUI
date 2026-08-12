package net.weyne1.easegui.client.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.api.EaseGUIScreenRegistry;
import net.weyne1.easegui.api.EaseGUIScreenType;
import net.weyne1.easegui.api.animation.AnimationProfile;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

import static net.weyne1.easegui.client.EaseGUIClient.LOGGER;

public class ConfigManager {
    private static final File CONFIG_FILE = new File(".", "config/easegui.json");
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();

    private static EaseGUIConfig currentConfig = EaseGUIConfigFactory.createDefaultConfig();
    private static boolean isLoaded = false;
    private static Screen cachedScreenInstance = null;
    private static EaseGUIScreenType cachedScreenType = EaseGUIScreenRegistry.OTHER;

    public static void load() {
        if (isLoaded) return;

        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                JsonObject jsonConfig = GSON.fromJson(reader, JsonObject.class);

                if (jsonConfig == null) {
                    jsonConfig = new JsonObject();
                }

                boolean migrated = false;

                int version = 0;
                if (jsonConfig.has("schemaVersion")) {
                    version = jsonConfig.get("schemaVersion").getAsInt();
                } else if (jsonConfig.has("schema_version")) {
                    version = jsonConfig.get("schema_version").getAsInt();
                }

                while (version < EaseGUIConfig.CURRENT_SCHEMA_VERSION) {
                    if (version == 0) {
                        migrated = ConfigMigrator.runMigrationV0toV1(jsonConfig);
                        version = 1;
                    } else if (version == 1) {
                        migrated |= ConfigMigrator.runMigrationV1toV2(jsonConfig);
                        version = 2;
                    } else if (version == 2) {
                        migrated |= ConfigMigrator.runMigrationV2toV3(jsonConfig);
                        version = 3;
                    } else {
                        break;
                    }
                }

                currentConfig = GSON.fromJson(jsonConfig, EaseGUIConfig.class);

                if (currentConfig == null) {
                    currentConfig = EaseGUIConfigFactory.createDefaultConfig();
                }

                currentConfig.schemaVersion = EaseGUIConfig.CURRENT_SCHEMA_VERSION;

                if (EaseGUIConfigFactory.mergeDefaults(currentConfig) || migrated) {
                    LOGGER.info("[EaseGUI] Config schema updated from version {} to {}.", version, EaseGUIConfig.CURRENT_SCHEMA_VERSION);
                    save();
                }

                LOGGER.info("[EaseGUI] Config successfully loaded from disk.");
            } catch (Exception e) {
                LOGGER.error("[EaseGUI] Failed to read config, creating default... Error: {}", e.getMessage());
                currentConfig = EaseGUIConfigFactory.createDefaultConfig();
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

    public static EaseGUIConfig getConfig() {
        if (!isLoaded) load();
        return currentConfig;
    }

    public static AnimationProfile getProfileForCurrentContext(WidgetCategory category) {
        if (category == null || category == WidgetCategory.UNKNOWN) return null;
        if (!isLoaded) load();

        Screen currentScreen = Minecraft.getInstance().gui.screen();

        if (currentScreen != cachedScreenInstance) {
            cachedScreenInstance = currentScreen;
            cachedScreenType = EaseGUIScreenRegistry.from(currentScreen);
        }

        EaseGUIConfig.ScreenSettings screenSettings = currentConfig.screens.get(cachedScreenType.getId());

        if (screenSettings != null) {
            if (!screenSettings.enabled) return null;

            AnimationProfile customProfile = screenSettings.customProfiles.get(category);
            if (customProfile != null) {
                return customProfile.isEnabled() ? customProfile : null;
            }
        }

        return currentConfig.global.elementProfiles.get(category);
    }

    @Nullable
    public static AnimationProfile getProfile(Screen screen, Function<EaseGUIConfig.ScreenSettings, AnimationProfile> profileExtractor) {
        EaseGUIConfig config = getConfig();
        if (!config.global.enabled) return null;

        EaseGUIScreenType type = EaseGUIScreenRegistry.from(screen);
        var settings = config.screens.get(type.getId());

        if (settings == null || !settings.enabled) return null;

        return profileExtractor.apply(settings);
    }
}