package net.weyne1.easegui.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;

public class ConfigMigrator {

    /**
     * Migrates the configuration schema from v0 (0.1.0) to v1 (0.2.0).
     * <ul>
     *     <li>Moves the legacy {@code logo.direction} field into {@code logo.logoProfile.cascadeDirection}.</li>
     *     <li>Forces {@code advancements.tabsProfile.cascadeDirection} to {@code LEFT_TO_RIGHT}.</li>
     *     <li>Disables animations for the experimental {@code other} screen category.</li>
     * </ul>
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

        if (screens.has("other") && screens.get("other").isJsonObject()) {
            JsonObject other = screens.getAsJsonObject("other");

            other.addProperty("enabled", false);
            changed = true;
        }

        return changed;
    }

    /**
     * Migrates the configuration schema from v1 (0.2.0) to v2 (0.3.0+).
     * <ul>
     * <li>Collapses flat {@code offsetX/Y} fields into a unified {@code offset} object.</li>
     * <li>Collapses flat {@code startScaleX/Y} fields into a unified {@code startScale} object.</li>
     * </ul>
     */
    static boolean runMigrationV1toV2(JsonObject jsonConfig) {
        return migrateProfilesRecursive(jsonConfig);
    }

    private static boolean migrateProfilesRecursive(JsonObject jsonObject) {
        boolean changed = false;

        if (jsonObject.has("offsetX") || jsonObject.has("offsetY")) {
            JsonObject offsetObj = new JsonObject();
            offsetObj.addProperty("x", jsonObject.has("offsetX") ? jsonObject.get("offsetX").getAsFloat() : 0f);
            offsetObj.addProperty("y", jsonObject.has("offsetY") ? jsonObject.get("offsetY").getAsFloat() : 0f);

            jsonObject.add("offset", offsetObj);
            jsonObject.remove("offsetX");
            jsonObject.remove("offsetY");
            changed = true;
        }

        if (jsonObject.has("startScaleX") || jsonObject.has("startScaleY")) {
            JsonObject scaleObj = new JsonObject();
            scaleObj.addProperty("x", jsonObject.has("startScaleX") ? jsonObject.get("startScaleX").getAsFloat() : 1f);
            scaleObj.addProperty("y", jsonObject.has("startScaleY") ? jsonObject.get("startScaleY").getAsFloat() : 1f);

            jsonObject.add("startScale", scaleObj);
            jsonObject.remove("startScaleX");
            jsonObject.remove("startScaleY");
            changed = true;
        }

        String[] keys = jsonObject.keySet().toArray(new String[0]);
        for (String key : keys) {
            JsonElement element = jsonObject.get(key);
            if (element != null && element.isJsonObject()) {
                if (migrateProfilesRecursive(element.getAsJsonObject())) {
                    changed = true;
                }
            }
        }

        return changed;
    }

    /**
     * Migrates configuration schema from v2 to v3.
     * Wraps single AnimationProfile structures into DirectionalAnimationProfile ("in" / "out").
     */
    static boolean runMigrationV2toV3(JsonObject jsonConfig) {
        boolean changed = false;

        if (jsonConfig.has("global") && jsonConfig.get("global").isJsonObject()) {
            JsonObject global = jsonConfig.getAsJsonObject("global");
            if (global.has("elementProfiles") && global.get("elementProfiles").isJsonObject()) {
                changed |= migrateProfileMap(global.getAsJsonObject("elementProfiles"));
            }
        }

        if (jsonConfig.has("screens") && jsonConfig.get("screens").isJsonObject()) {
            JsonObject screens = jsonConfig.getAsJsonObject("screens");

            for (String screenId : screens.keySet()) {
                JsonElement screenElement = screens.get(screenId);
                if (screenElement != null && screenElement.isJsonObject()) {
                    JsonObject screenObj = screenElement.getAsJsonObject();

                    if (screenObj.has("customProfiles") && screenObj.get("customProfiles").isJsonObject()) {
                        changed |= migrateProfileMap(screenObj.getAsJsonObject("customProfiles"));
                    }

                    if ("title".equals(screenId) && screenObj.has("logo") && screenObj.get("logo").isJsonObject()) {
                        JsonObject logoObj = screenObj.getAsJsonObject("logo");
                        changed |= wrapIfV2Profile(logoObj, "logoProfile");
                        changed |= wrapIfV2Profile(logoObj, "editionProfile");
                    }

                    if ("advancements".equals(screenId) && screenObj.has("advancements") && screenObj.get("advancements").isJsonObject()) {
                        JsonObject advObj = screenObj.getAsJsonObject("advancements");
                        changed |= wrapIfV2Profile(advObj, "windowProfile");
                        changed |= wrapIfV2Profile(advObj, "tabsProfile");
                    }
                }
            }
        }

        return changed;
    }

    private static boolean migrateProfileMap(JsonObject mapObj) {
        boolean changed = false;
        for (String key : new HashSet<>(mapObj.keySet())) {
            JsonElement elem = mapObj.get(key);
            if (elem != null && elem.isJsonObject()) {
                if (wrapSingleProfileToDirectional(elem.getAsJsonObject())) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    private static boolean wrapIfV2Profile(JsonObject parent, String key) {
        if (!parent.has(key) || !parent.get(key).isJsonObject()) return false;
        return wrapSingleProfileToDirectional(parent.getAsJsonObject(key));
    }

    private static boolean wrapSingleProfileToDirectional(JsonObject profileObj) {
        if (profileObj.has("in") || profileObj.has("out") || profileObj.keySet().isEmpty()) {
            return false;
        }

        JsonObject inObj = profileObj.deepCopy();
        for (String k : new HashSet<>(profileObj.keySet())) {
            profileObj.remove(k);
        }
        profileObj.add("in", inObj);
        return true;
    }
}
