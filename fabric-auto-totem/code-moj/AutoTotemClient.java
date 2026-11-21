package com.nth.autototem;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AutoTotemClient {

    private static AutoTotemConfig config;
    private static int ticksSincePop = 0;

    public static void onInitializeClient() {
        config = AutoTotemConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(AutoTotemClient::onTick);
    }

    public static AutoTotemConfig getConfig() {
        return config;
    }

    private static void onTick(Minecraft client) {
        if (!config.enabled || client.player == null || client.gameMode == null || client.level == null) {
            ticksSincePop = 0;
            return;
        }
        refillOffhand(client);
    }

    private static void refillOffhand(Minecraft client) {
        if (client.player.isSpectator()) {
            return;
        }

        if (client.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) {
            ticksSincePop = 0;
            return;
        }

        if (ticksSincePop < config.delayTicks) {
            ticksSincePop++;
            return;
        }

        if (!client.player.inventoryMenu.getCarried().isEmpty()) {
            return;
        }

        int inventorySlot = findTotemSlot(client);
        if (inventorySlot == -1) {
            return;
        }

        int containerSlot = toContainerSlot(inventorySlot);
        int containerId = client.player.inventoryMenu.containerId;
        int shieldSlot = InventoryMenu.SHIELD_SLOT;
        client.gameMode.handleContainerInput(containerId, containerSlot, 0, ContainerInput.PICKUP, client.player);
        client.gameMode.handleContainerInput(containerId, shieldSlot, 0, ContainerInput.PICKUP, client.player);
        client.gameMode.handleContainerInput(containerId, containerSlot, 0, ContainerInput.PICKUP, client.player);
        ticksSincePop = 0;
    }

    private static int findTotemSlot(Minecraft client) {
        for (int i = 8; i >= 0; i--) {
            if (client.player.getInventory().getItem(i).is(Items.TOTEM_OF_UNDYING)) {
                return i;
            }
        }
        for (int i = 9; i < 36; i++) {
            if (client.player.getInventory().getItem(i).is(Items.TOTEM_OF_UNDYING)) {
                return i;
            }
        }
        return -1;
    }

    private static int toContainerSlot(int inventorySlot) {
        if (inventorySlot < 9) {
            return 36 + inventorySlot;
        }
        return inventorySlot;
    }

    public static void renderHudLabel(GuiGraphicsExtractor graphics) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }

        String label = "AutoTotem [" + config.delayTicks + "t]";
        int x = client.getWindow().getGuiScaledWidth() - 5 - client.font.width(label);
        int color = config.enabled ? 0xFF55FF55 : 0xFF555555;
        graphics.text(client.font, label, x, 5, color);
    }
}
