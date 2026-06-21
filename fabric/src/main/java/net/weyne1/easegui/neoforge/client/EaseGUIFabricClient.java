package net.weyne1.easegui.neoforge.client;

import net.fabricmc.api.ClientModInitializer;
import net.weyne1.easegui.client.EaseGUIClient;

public class EaseGUIFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EaseGUIClient.init();
	}
}