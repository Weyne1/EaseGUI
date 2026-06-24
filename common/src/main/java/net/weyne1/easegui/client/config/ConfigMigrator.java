package net.weyne1.easegui.client.config;

import com.google.gson.JsonObject;

public class ConfigMigrator {

    /**
     * Migrates the configuration schema from v0 (0.1.0) to v1 (0.2.0+).
     * <ul>
     * <li>Moves the legacy {@code logo.direction} field into {@code logo.logoProfile.cascadeDirection}.</li>
     * <li>Forces {@code advancements.tabsProfile.cascadeDirection} to {@code LEFT_TO_RIGHT} to fix legacy GUI layout softlocks.</li>
     * </ul>
     * @param jsonConfig the raw JSON configuration root
     * @return {@code true} if the JSON structure was modified, {@code false} otherwise
     */
    static boolean runMigrationV0toV1(JsonObject jsonConfig) {
        boolean changed = false;

        if (!jsonConfig.has("screens") || !jsonConfig.get("screens").isJsonObject()) {
            return false;
        }
        JsonObject screens = jsonConfig.getAsJsonObject("screens");

        if (screens.has("title") && screens.get("title").isJsonObject()) {
            JsonObject title = screens.getAsJsonObject("title");
            if (title.has("logo") && title.get("logo").isJsonObject()) {
                JsonObject logo = title.getAsJsonObject("logo");
                if (logo.has("direction")) {
                    String oldDirection = logo.get("direction").getAsString();

                    if (!logo.has("logoProfile") || !logo.get("logoProfile").isJsonObject()) {
                        logo.add("logoProfile", new JsonObject());
                    }
                    JsonObject logoProfile = logo.getAsJsonObject("logoProfile");

                    logoProfile.addProperty("cascadeDirection", oldDirection);

                    logo.remove("direction");
                    changed = true;
                }
            }
        }

        if (screens.has("advancements") && screens.get("advancements").isJsonObject()) {
            JsonObject advancementsScreen = screens.getAsJsonObject("advancements");
            if (advancementsScreen.has("advancements") && advancementsScreen.get("advancements").isJsonObject()) {
                JsonObject advSettings = advancementsScreen.getAsJsonObject("advancements");
                if (advSettings.has("tabsProfile") && advSettings.get("tabsProfile").isJsonObject()) {
                    JsonObject tabsProfile = advSettings.getAsJsonObject("tabsProfile");

                    tabsProfile.addProperty("cascadeDirection", "LEFT_TO_RIGHT");
                    changed = true;
                }
            }
        }

        return changed;
    }
}
