package net.weyne1.easegui.client.config;

import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerReconfigScreen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.gui.screens.worldselection.*;
import net.weyne1.easegui.client.gui.screens.EaseGUIAbstractSplitScreen;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry that maps Minecraft Screen classes to ScreenType definitions.
 *
 * <p>This is used by EaseGUI to decide how different screens should be categorized
 * and animated. Matching works in two steps:
 * <ul>
 *   <li>Fast exact class lookup (cache)</li>
 *   <li>Fallback hierarchy scan using isAssignableFrom</li>
 * </ul>
 *
 * <p>Mod developers can register custom screens to integrate them into animation system.
 */
public final class EaseGUIScreenRegistry {

    /** Cache for exact class → ScreenType mapping to avoid hierarchy checks */
    private static final Map<Class<? extends Screen>, ScreenType> EXACT_MATCH_CACHE = new ConcurrentHashMap<>();

    /** Sorted list of screen types used for inheritance-based matching */
    private static final List<ScreenType> HIERARCHY_LIST = new ArrayList<>();

    /** Fallback type used when no match is found */
    public static final ScreenType OTHER = new ScreenType("other", Screen.class, Integer.MIN_VALUE, ScreenGroup.OTHER, false);

    static {
        // Vanilla UI screens
        register("title", TitleScreen.class, 1000, ScreenGroup.BASIC);
        register("options", OptionsScreen.class, 1000, ScreenGroup.BASIC);
        register("options_sub", OptionsSubScreen.class, 1000, ScreenGroup.BASIC);
        register("pack_selection", PackSelectionScreen.class, 1000, ScreenGroup.BASIC);
        register("advancements", AdvancementsScreen.class, 1000, ScreenGroup.BASIC);
        register("statistics", StatsScreen.class, 1000, ScreenGroup.BASIC);
        register("warning", WarningScreen.class, 1000, ScreenGroup.BASIC);
        register("pause", PauseScreen.class, 1000, ScreenGroup.BASIC);
        register("share_to_lan", ShareToLanScreen.class, 1000, ScreenGroup.BASIC);
        register("death", DeathScreen.class, 1000, ScreenGroup.BASIC);
        register("social_interactions", SocialInteractionsScreen.class, 1000, ScreenGroup.BASIC);

        // Editors
        register("sign_edit", AbstractSignEditScreen.class, 1000, ScreenGroup.EDITORS);
        register("book_edit", BookEditScreen.class, 1000, ScreenGroup.EDITORS);
        register("book_view", BookViewScreen.class, 1000, ScreenGroup.EDITORS);
        register("command_block_edit", AbstractCommandBlockEditScreen.class, 1000, ScreenGroup.EDITORS);
        register("structure_block_edit", StructureBlockEditScreen.class, 1000, ScreenGroup.EDITORS);
        register("jigsaw_block_edit", JigsawBlockEditScreen.class, 1000, ScreenGroup.EDITORS);

        // World / multiplayer menus
        register("world_selection", SelectWorldScreen.class, 1000, ScreenGroup.WORLDS);
        register("server_selection", JoinMultiplayerScreen.class, 1000, ScreenGroup.WORLDS);
        register("realms_main", RealmsMainScreen.class, 1000, ScreenGroup.WORLDS);
        register("create_world", CreateWorldScreen.class, 1000, ScreenGroup.WORLDS);
        register("create_flat_world", CreateFlatWorldScreen.class, 1000, ScreenGroup.WORLDS);
        register("direct_join_server", DirectJoinServerScreen.class, 1000, ScreenGroup.WORLDS);
        register("edit_world", EditWorldScreen.class, 1000, ScreenGroup.WORLDS);
        register("edit_server", EditServerScreen.class, 1000, ScreenGroup.WORLDS);
        register("edit_game_rules", EditGameRulesScreen.class, 1000, ScreenGroup.WORLDS);
        register("experiments", ExperimentsScreen.class, 1000, ScreenGroup.WORLDS);
        register("connecting", ConnectScreen.class, 1000, ScreenGroup.WORLDS);
        register("disconnected", DisconnectedScreen.class, 1000, ScreenGroup.WORLDS);
        register("server_reconfig", ServerReconfigScreen.class, 1000, ScreenGroup.WORLDS);
        register("credits", CreditsAndAttributionScreen.class, 1000, ScreenGroup.WORLDS);

        // Containers / inventories
        register("creative_inventory", CreativeModeInventoryScreen.class, 500, ScreenGroup.CONTAINERS);
        register("survival_inventory", InventoryScreen.class, 500, ScreenGroup.CONTAINERS);
        register("anvil", AnvilScreen.class, 500, ScreenGroup.CONTAINERS);
        register("enchanting_table", EnchantmentScreen.class, 500, ScreenGroup.CONTAINERS);
        register("container", ContainerScreen.class, 500, ScreenGroup.CONTAINERS);
        register("smithing", SmithingScreen.class, 500, ScreenGroup.CONTAINERS);
        register("dispenser", DispenserScreen.class, 500, ScreenGroup.CONTAINERS);
        register("beacon", BeaconScreen.class, 500, ScreenGroup.CONTAINERS);
        register("crafter", CrafterScreen.class, 500, ScreenGroup.CONTAINERS);
        register("crafting", CraftingScreen.class, 500, ScreenGroup.CONTAINERS);
        register("brewing_stand", BrewingStandScreen.class, 500, ScreenGroup.CONTAINERS);
        register("cartography_table", CartographyTableScreen.class, 500, ScreenGroup.CONTAINERS);
        register("furnace", AbstractFurnaceScreen.class, 500, ScreenGroup.CONTAINERS);
        register("grindstone", GrindstoneScreen.class, 500, ScreenGroup.CONTAINERS);
        register("hopper", HopperScreen.class, 500, ScreenGroup.CONTAINERS);
        register("horse_inventory", HorseInventoryScreen.class, 500, ScreenGroup.CONTAINERS);
        register("lectern", LecternScreen.class, 500, ScreenGroup.CONTAINERS);
        register("loom", LoomScreen.class, 500, ScreenGroup.CONTAINERS);
        register("shulker_box", ShulkerBoxScreen.class, 500, ScreenGroup.CONTAINERS);
        register("stonecutter", StonecutterScreen.class, 500, ScreenGroup.CONTAINERS);
        register("other_containers", AbstractContainerScreen.class, 100, ScreenGroup.CONTAINERS);

        // Internal config UI
        register("ease_gui_config", EaseGUIAbstractSplitScreen.class, 100, ScreenGroup.OTHER);

        // Optional mod integration (Mod Menu)
        try {
            Class<?> modMenuScreenClass = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
            register("modmenu", modMenuScreenClass.asSubclass(Screen.class), 1000, ScreenGroup.BASIC, false);
        } catch (ClassNotFoundException ignored) {
            // Mod Menu is not installed — safely skip registration
        }
    }

    /**
     * Registers a screen type with default enabled state (true).
     */
    public static synchronized void register(
            String id,
            Class<? extends Screen> screenClass,
            int priority,
            ScreenGroup category
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
            ScreenGroup category,
            boolean enabledByDefault
    ) {
        ScreenType type = new ScreenType(id, screenClass, priority, category, enabledByDefault);

        EXACT_MATCH_CACHE.put(screenClass, type);

        HIERARCHY_LIST.removeIf(t -> t.getId().equals(id));
        HIERARCHY_LIST.add(type);
        HIERARCHY_LIST.sort(Comparator.comparingInt(ScreenType::getPriority).reversed());
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
    public static ScreenType from(Screen screen) {
        if (screen == null) return OTHER;

        Class<? extends Screen> screenClass = screen.getClass();

        ScreenType exact = EXACT_MATCH_CACHE.get(screenClass);
        if (exact != null) return exact;

        for (ScreenType type : HIERARCHY_LIST) {
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
    public static Collection<ScreenType> getRegisteredTypes() {
        return Collections.unmodifiableList(HIERARCHY_LIST);
    }
}