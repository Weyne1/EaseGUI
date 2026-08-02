package net.weyne1.easegui.api;

import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.gui.screens.worldselection.*;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.gui.screens.EaseGUIAbstractSplitScreen;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Registry that maps Minecraft Screen classes to ScreenType definitions.
 *
 * <p>This is used by EaseGUI to decide how different screens should be categorized
 * and animated. Matching works in two steps:
 * <ul>
 * <li>Fast exact class lookup (cache)</li>
 * <li>Fallback hierarchy scan using isAssignableFrom</li>
 * </ul>
 *
 * <p>Mod developers can register custom screens and default animation parameters
 * to integrate them into the EaseGUI animation system.
 */
public final class EaseGUIScreenRegistry {

    private static final Map<Class<? extends Screen>, EaseGUIScreenType> EXACT_MATCH_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Consumer<EaseGUIConfig.ScreenSettings>> DEFAULT_CONFIGURATORS = new ConcurrentHashMap<>();
    private static final List<EaseGUIScreenType> HIERARCHY_LIST = new ArrayList<>();

    public static final EaseGUIScreenType OTHER = new EaseGUIScreenType("other", Screen.class, Integer.MIN_VALUE, EaseGUIScreenGroup.OTHER, false);

    static {
        // Vanilla UI screens
        register("title", TitleScreen.class, 1000, EaseGUIScreenGroup.BASIC);
        register("options", OptionsScreen.class, 1000, EaseGUIScreenGroup.BASIC);
        register("options_sub", OptionsSubScreen.class, 1000, EaseGUIScreenGroup.BASIC);
        register("pack_selection", PackSelectionScreen.class, 1000, EaseGUIScreenGroup.BASIC);
        register("advancements", AdvancementsScreen.class, 1000, EaseGUIScreenGroup.BASIC);
        register("statistics", StatsScreen.class, 1000, EaseGUIScreenGroup.BASIC);
        register("warning", WarningScreen.class, 1000, EaseGUIScreenGroup.BASIC);
        register("pause", PauseScreen.class, 1000, EaseGUIScreenGroup.BASIC);
        register("share_to_lan", ShareToLanScreen.class, 1000, EaseGUIScreenGroup.BASIC);
        register("death", DeathScreen.class, 1000, EaseGUIScreenGroup.BASIC);
        register("social_interactions", SocialInteractionsScreen.class, 1000, EaseGUIScreenGroup.BASIC);

        // Editors
        register("sign_edit", AbstractSignEditScreen.class, 1000, EaseGUIScreenGroup.EDITORS);
        register("book_edit", BookEditScreen.class, 1000, EaseGUIScreenGroup.EDITORS);
        register("book_view", BookViewScreen.class, 1000, EaseGUIScreenGroup.EDITORS);
        register("command_block_edit", AbstractCommandBlockEditScreen.class, 1000, EaseGUIScreenGroup.EDITORS);
        register("structure_block_edit", StructureBlockEditScreen.class, 1000, EaseGUIScreenGroup.EDITORS);
        register("jigsaw_block_edit", JigsawBlockEditScreen.class, 1000, EaseGUIScreenGroup.EDITORS);

        // World / multiplayer menus
        register("world_selection", SelectWorldScreen.class, 1000, EaseGUIScreenGroup.WORLDS);
        register("server_selection", JoinMultiplayerScreen.class, 1000, EaseGUIScreenGroup.WORLDS);
        register("realms_main", RealmsMainScreen.class, 1000, EaseGUIScreenGroup.WORLDS);
        register("create_world", CreateWorldScreen.class, 1000, EaseGUIScreenGroup.WORLDS);
        register("create_flat_world", CreateFlatWorldScreen.class, 1000, EaseGUIScreenGroup.WORLDS);
        register("direct_join_server", DirectJoinServerScreen.class, 1000, EaseGUIScreenGroup.WORLDS);
        register("edit_world", EditWorldScreen.class, 1000, EaseGUIScreenGroup.WORLDS);
        register("edit_server", EditServerScreen.class, 1000, EaseGUIScreenGroup.WORLDS);
        register("edit_game_rules", EditGameRulesScreen.class, 1000, EaseGUIScreenGroup.WORLDS);
        register("experiments", ExperimentsScreen.class, 1000, EaseGUIScreenGroup.WORLDS);
        register("connecting", ConnectScreen.class, 1000, EaseGUIScreenGroup.WORLDS);
        register("disconnected", DisconnectedScreen.class, 1000, EaseGUIScreenGroup.WORLDS);
        register("credits", CreditsAndAttributionScreen.class, 1000, EaseGUIScreenGroup.WORLDS);

        // Containers / inventories
        register("creative_inventory", CreativeModeInventoryScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("survival_inventory", InventoryScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("anvil", AnvilScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("enchanting_table", EnchantmentScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("container", ContainerScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("smithing", SmithingScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("dispenser", DispenserScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("beacon", BeaconScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("crafting", CraftingScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("brewing_stand", BrewingStandScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("cartography_table", CartographyTableScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("furnace", AbstractFurnaceScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("grindstone", GrindstoneScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("hopper", HopperScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("horse_inventory", HorseInventoryScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("lectern", LecternScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("loom", LoomScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("shulker_box", ShulkerBoxScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("stonecutter", StonecutterScreen.class, 500, EaseGUIScreenGroup.CONTAINERS);
        register("other_containers", AbstractContainerScreen.class, 100, EaseGUIScreenGroup.CONTAINERS);

        // Internal config UI
        register("ease_gui_config", EaseGUIAbstractSplitScreen.class, 100, EaseGUIScreenGroup.OTHER);

        // Optional mod integration (Mod Menu)
        try {
            Class<?> modMenuScreenClass = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
            register("modmenu", modMenuScreenClass.asSubclass(Screen.class), 1000, EaseGUIScreenGroup.BASIC, false);
        } catch (ClassNotFoundException ignored) {
            // Mod Menu is not installed — safely skip registration
        }
    }

    /**
     * Registers a custom configurator callback for a specific screen ID.
     * This is intended for third-party developers to populate custom default configurations.
     *
     * @param id the unique screen ID (e.g. "mymod:grinder")
     * @param configurator the consumer that configures the default {@link EaseGUIConfig.ScreenSettings}
     */
    @SuppressWarnings("unused")
    public static void registerConfigurator(String id, Consumer<EaseGUIConfig.ScreenSettings> configurator) {
        DEFAULT_CONFIGURATORS.put(id, configurator);
    }

    /**
     * Configures the screen settings with registered developer defaults.
     * Primarily called by the config factory during clean setup initialization.
     *
     * @param id the screen identifier
     * @param settings the target settings to configure
     */
    public static void configureDefaults(String id, EaseGUIConfig.ScreenSettings settings) {
        Consumer<EaseGUIConfig.ScreenSettings> configurator = DEFAULT_CONFIGURATORS.get(id);
        if (configurator != null) {
            configurator.accept(settings);
        }
    }

    /**
     * Patches the existing screen settings with newly introduced developer defaults.
     * Ensures that new categories or default entries are added safely without
     * overwriting any manual modifications made by the user in the config file.
     *
     * @param id the screen identifier
     * @param settings the user's existing settings
     * @return true if the settings were modified and need to be saved
     */
    public static boolean patchDefaults(String id, EaseGUIConfig.ScreenSettings settings) {
        Consumer<EaseGUIConfig.ScreenSettings> configurator = DEFAULT_CONFIGURATORS.get(id);
        if (configurator == null) return false;

        EaseGUIConfig.ScreenSettings defaultSettings = new EaseGUIConfig.ScreenSettings();
        configurator.accept(defaultSettings);

        boolean changed = false;

        for (Map.Entry<WidgetCategory, AnimationProfile> entry : defaultSettings.customProfiles.entrySet()) {
            if (!settings.customProfiles.containsKey(entry.getKey())) {
                settings.customProfiles.put(entry.getKey(), entry.getValue());
                changed = true;
            }
        }

        return changed;
    }

    /**
     * Registers a screen type with default enabled state (true).
     */
    public static synchronized void register(
            String id,
            Class<? extends Screen> screenClass,
            int priority,
            EaseGUIScreenGroup category
    ) {
        register(id, screenClass, priority, category, true);
    }

    /**
     * Registers a screen type for animation classification.
     *
     * @param id unique identifier (e.g. "inventory" or "modid:screen")
     * @param screenClass target screen class
     * @param priority matching priority (higher = checked earlier)
     * @param category logical grouping of screen type
     * @param enabledByDefault whether this screen type is enabled on first config creation
     */
    public static synchronized void register(
            String id,
            Class<? extends Screen> screenClass,
            int priority,
            EaseGUIScreenGroup category,
            boolean enabledByDefault
    ) {
        EaseGUIScreenType type = new EaseGUIScreenType(id, screenClass, priority, category, enabledByDefault);

        EXACT_MATCH_CACHE.put(screenClass, type);

        HIERARCHY_LIST.removeIf(t -> t.getId().equals(id));
        HIERARCHY_LIST.add(type);
        HIERARCHY_LIST.sort(Comparator.comparingInt(EaseGUIScreenType::getPriority).reversed());
    }

    /**
     * Finds a ScreenType for a runtime screen instance.
     *
     * <p>Resolution order:
     * <ol>
     *   <li>Exact class match (fast path)</li>
     *   <li>Assignable-from hierarchy scan</li>
     *   <li>Fallback to OTHER</li>
     * </ol>
     */
    public static EaseGUIScreenType from(Screen screen) {
        if (screen == null) return OTHER;

        Class<? extends Screen> screenClass = screen.getClass();

        EaseGUIScreenType exact = EXACT_MATCH_CACHE.get(screenClass);
        if (exact != null) return exact;

        for (EaseGUIScreenType type : HIERARCHY_LIST) {
            if (type.getScreenClass().isAssignableFrom(screenClass)) {
                EXACT_MATCH_CACHE.put(screenClass, type);
                return type;
            }
        }

        return OTHER;
    }

    /**
     * Returns all registered screen types (read-only view).
     */
    public static Collection<EaseGUIScreenType> getRegisteredTypes() {
        return Collections.unmodifiableList(HIERARCHY_LIST);
    }
}