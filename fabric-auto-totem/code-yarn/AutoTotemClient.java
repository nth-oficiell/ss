package com.nth.autototem;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

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

    private static void onTick(MinecraftClient client) {
        if (!config.enabled || client.player == null || client.interactionManager == null || client.world == null) {
            ticksSincePop = 0;
            return;
        }
        refillOffhand(client);
    }

    private static void refillOffhand(MinecraftClient client) {
        if (client.player.isSpectator()) {
            return;
        }

        if (client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
            ticksSincePop = 0;
            return;
        }

        if (ticksSincePop < config.delayTicks) {
            ticksSincePop++;
            return;
        }

        if (!client.player.currentScreenHandler.getCursorStack().isEmpty()) {
            return;
        }

        int inventorySlot = findTotemSlot(client);
        if (inventorySlot == -1) {
            return;
        }

        int handlerSlot = toHandlerSlot(inventorySlot);
        int syncId = client.player.playerScreenHandler.syncId;
        client.interactionManager.clickSlot(syncId, handlerSlot, 0, SlotActionType.PICKUP, client.player);
        client.interactionManager.clickSlot(syncId, 45, 0, SlotActionType.PICKUP, client.player);
        client.interactionManager.clickSlot(syncId, handlerSlot, 0, SlotActionType.PICKUP, client.player);
        ticksSincePop = 0;
    }

    private static int findTotemSlot(MinecraftClient client) {
        for (int i = 8; i >= 0; i--) {
            if (client.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                return i;
            }
        }
        for (int i = 9; i < 36; i++) {
            if (client.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                return i;
            }
        }
        return -1;
    }

    private static int toHandlerSlot(int inventorySlot) {
        if (inventorySlot < 9) {
            return 36 + inventorySlot;
        }
        return inventorySlot;
    }

    public static void renderHudLabel(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden || client.player == null) {
            return;
        }

        String label = "AutoTotem [" + config.delayTicks + "t]";
        int x = client.getWindow().getScaledWidth() - 5 - client.textRenderer.getWidth(label);
        int color = config.enabled ? 0xFF55FF55 : 0xFF555555;
        context.drawTextWithShadow(client.textRenderer, label, x, 5, color);
    }
}
