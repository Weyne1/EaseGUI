package net.weyne1.easegui.client.config;

public enum ScreenGroup {
    BASIC("easegui.screen_group.basic"),
    EDITORS("easegui.screen_group.editors"),
    WORLDS("easegui.screen_group.worlds"),
    CONTAINERS("easegui.screen_group.containers"),
    OTHER("easegui.screen_group.other");

    private final String translationKey;

    ScreenGroup(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}