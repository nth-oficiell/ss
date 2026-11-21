package com.nth.autototem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class AutoTotemHud implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoTotemClient.onInitializeClient();
        AutoTotemKeys.register();

        HudRenderCallback.EVENT.register((context, delta) ->
                AutoTotemClient.renderHudLabel(context));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (AutoTotemKeys.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new AutoTotemScreen(null));
                }
            }
        });
    }
}
