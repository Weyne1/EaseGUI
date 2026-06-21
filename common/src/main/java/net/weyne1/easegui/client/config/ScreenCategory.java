package net.weyne1.easegui.client.config;

public enum ScreenCategory {
    BASIC("easegui.screen_category.basic"),
    EDITORS("easegui.screen_category.editors"),
    WORLDS("easegui.screen_category.worlds"),
    CONTAINERS("easegui.screen_category.containers"),
    OTHER("easegui.screen_category.other");

    private final String translationKey;

    ScreenCategory(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}