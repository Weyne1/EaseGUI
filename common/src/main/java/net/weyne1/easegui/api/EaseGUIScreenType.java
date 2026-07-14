package net.weyne1.easegui.api;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Describes a Minecraft Screen type used by EaseGUI.
 *
 * <p>This class is a mapping between a Screen class and its metadata:
 * category, priority, localization key, and default enabled state.
 *
 * <p>It is used for:
 * <ul>
 *   <li>screen classification</li>
 *   <li>animation selection</li>
 *   <li>UI grouping</li>
 * </ul>
 */
public final class EaseGUIScreenType {

    private final String id;
    private final Class<? extends Screen> screenClass;
    private final String translationKey;
    private final int priority;
    private final EaseGUIScreenGroup group;
    private final boolean enabledByDefault;

    /**
     * Creates a screen type definition.
     *
     * <p>Translation key rules:
     * <ul>
     *   <li>"title" → "easegui.screen_type.title"</li>
     *   <li>"modid:screen" → "modid.screen_type.screen"</li>
     * </ul>
     */
    public EaseGUIScreenType(
            String id,
            Class<? extends Screen> screenClass,
            int priority,
            EaseGUIScreenGroup group,
            boolean enabledByDefault
    ) {
        this.id = id;
        this.screenClass = screenClass;
        this.priority = priority;
        this.group = group;
        this.enabledByDefault = enabledByDefault;

        if (id.contains(":")) {
            String[] parts = id.split(":", 2);
            this.translationKey = parts[0] + ".screen_type." + parts[1];
        } else {
            this.translationKey = "easegui.screen_type." + id;
        }
    }

    /**
     * Creates a screen type enabled by default.
     */
    @SuppressWarnings("unused")
    public EaseGUIScreenType(
            String id,
            Class<? extends Screen> screenClass,
            int priority,
            EaseGUIScreenGroup group
    ) {
        this(id, screenClass, priority, group, true);
    }

    public String getId() {
        return id;
    }

    public Class<? extends Screen> getScreenClass() {
        return screenClass;
    }

    public int getPriority() {
        return priority;
    }

    public EaseGUIScreenGroup getGroup() {
        return group;
    }

    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }

    /**
     * Returns localized display name for UI.
     */
    public MutableComponent getDisplayName() {
        return Component.translatable(translationKey);
    }
}