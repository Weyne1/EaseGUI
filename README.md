![Minecraft GUI is now animated.](https://cdn.modrinth.com/data/cached_images/c31bc4f45e41d14cb3949049daec3450426fc03b.gif)

<div align="center">

## EaseGUI is a client-side mod that adds smooth, dynamic entry [animations](https://modrinth.com/project/easegui/gallery) to Minecraft screens and interfaces!

<img src="https://cdn.modrinth.com/data/zS8uflwP/images/47e1d2c7fc9a70215333c06f2e9c81b1653eb006.webp" alt="Showcase" width="90%">

</div>


---

### Animated GUI Elements:
- Smooth background blur
- Main menu titles & logos
- Buttons
- Text labels
- Scrollable lists
- Containers & inventory screens

## Deep Customization
The mod offers extensive customization options for every element category. You can configure animations globally for all screens or fine-tune them individually for specific menus to suit your taste.

> [!IMPORTANT]
> (For Fabric) To customize your animations easily in-game without editing config files manually, it is highly recommended to install [Mod Menu](https://modrinth.com/mod/modmenu).

<details>
<summary>⚙ Configuration Parameters Explained</summary>

- **Animation [ON/OFF]** — Toggles animations for the selected element category.
- **Duration** — Controls how long the animation lasts (in milliseconds).
- **Offset [X/Y]** — Sets the starting position of the element relative to its final layout position.
- **Initial Scale [X/Y]** — Controls the scale of the element at the beginning of the animation.
- **Initial Alpha** — Adjusts the starting opacity (transparency) of the element.
- **Cascade Delay** — The time delay between animating consecutive elements of the same type (creates a beautiful staggered entry effect).
- **Cascade Order** — The direction of the staggered sequence: Top-to-Bottom or Bottom-to-Top.
- **Pivot Point** — The anchor point used for scaling transformations (e.g., scaling from the center, top-left, etc.).
- **Easing (Interpolation)** — The mathematical curve that defines the acceleration profile and overall feel of the animation.

</details>

By default, a clean and simplified global animation preset is applied. However, you can craft **completely unique transitions** for each screen or disable them entirely where they aren't needed.

<div align="center">

<img src="https://cdn.modrinth.com/data/zS8uflwP/images/d1c3bc54e85e91ce8a84a332abf0a325bfd0bd5d.webp" alt="Container Animations Showcase" width="90%">

</div>

## 🧪 Beta Status & Feedback

EaseGUI is currently in **Beta**. While the core features are stable and fully functional on both Fabric and NeoForge, there is still a massive world of third-party mods with unique interfaces out there.

As the developer, **I would be incredibly grateful for your bug reports!** If you run into compatibility issues with other mods, please let me know.

## Compatibility & Known Issues
**While EaseGUI is engineered to inject animations as cleanly as possible without conflicting with other mods, unforeseen edge cases can happen:**

- Some custom screens (especially complex ones from third-party mods) might **not support animations** due to their unique rendering implementations. This mod specifically targets standard, vanilla-aligned GUI components.
- Mods that implement entirely custom GUI rendering systems outside of the standard pipeline (such as Sodium's video options menu) will not be animated.

If you encounter visual glitches or rendering issues with third-party screens, you don't have to turn off the whole mod! You can easily disable animations for all custom interfaces at once in the config (under the **"Other Screens"** tab) while keeping the smooth transitions across all vanilla menus.

## Gradle commands

Build: `./gradlew build`  
Build for a specific platform:
* Fabric: `./gradlew :fabric:build`
* NeoForge:  `./gradlew :neoforge:build`

