package net.weyne1.easegui.neoforge.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.weyne1.easegui.client.gui.screens.EaseGUIMainConfigScreen;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return EaseGUIMainConfigScreen::new;
    }
}