<div align="center">
  <h3>EaseGUI is a client-side mod that adds smooth, dynamic entry animations to Minecraft screens and interfaces!</h3>

  [![License](https://img.shields.io/github/license/Weyne1/easegui.svg)](https://github.com/Weyne1/EaseGUI/blob/master/LICENSE)
  [![MC Versions](https://cf.way2muchnoise.eu/versions/For%20MC_1584306_all.svg)](https://www.curseforge.com/minecraft/mc-mods/easegui)
  [![CurseForge Downloads](https://cf.way2muchnoise.eu/full_1584306_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/easegui)
  [![Modrinth Downloads](https://img.shields.io/modrinth/dt/zS8uflwP?label=Modrinth%20Downloads&color=00AF5C)](https://modrinth.com/mod/easegui)
</div>

<p align="center">
  <img src="gallery/menus.webp" width="97%"/>
  <img src="gallery/in-game.webp" width="32%"/>
  <img src="gallery/containers.webp" width="32%"/>
  <img src="gallery/customization.webp" width="32%"/>
</p>

---

<div align="center">
  <h2>Animated Elements</h2>
  <b>Background Blur • Minecraft Logo • Buttons • Text Labels • Scrollable Lists • Containers</b>

  <h2>Fabric Requirements</h2>
</div>

<p align="center">
  <a href="https://modrinth.com/mod/fabric-api" target="_blank">
    <img src="gallery/fabric-api.png" alt="Fabric API" width="200" style="margin: 0 4px;">
  </a>
  <a href="https://modrinth.com/mod/modmenu" target="_blank">
    <img src="gallery/mod-menu.png" alt="Mod Menu" width="200" style="margin: 0 4px;">
  </a>
</p>

## Building

JDK version depends on the MC version

Build: `./gradlew build`  
Build for a specific platform:

* Fabric: `./gradlew :fabric:build`
* (Neo)Forge:  `./gradlew :neoforge:build`

## Contributing

### 🌐 Translations

> You can help by adding a translation of EaseGUI into your language!
> - Just add a new `.json` file in `src/main/resources/assets/easegui/lang/` folder (e.g., `ru_ru.json`, `es_es.json`)
> - You can copy `en_us.json` as a template (make sure it's copied from the latest version branch)
> - Open a PR or just send the file in a GitHub Issues, and I'll add it!