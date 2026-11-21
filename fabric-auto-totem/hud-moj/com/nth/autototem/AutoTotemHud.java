package com.nth.autototem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class AutoTotemHud implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoTotemClient.onInitializeClient();
        AutoTotemKeys.register();

        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath("autototem", "hud"),
                (graphics, tracker) -> AutoTotemClient.renderHudLabel(graphics)
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (AutoTotemKeys.wasPressed()) {
                client.setScreenAndShow(new AutoTotemScreen(null));
            }
        });
    }
}
