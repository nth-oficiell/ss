package com.nth.autototem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AutoTotemClient implements ClientModInitializer {

    private static AutoTotemConfig config;
    private static KeyBinding toggleKey;
    private static KeyBinding delayUpKey;
    private static KeyBinding delayDownKey;
    private static int ticksSincePop = 0;

    @Override
    public void onInitializeClient() {
        config = AutoTotemConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autototem.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.autototem"
        ));
        delayUpKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autototem.delay_up",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_BRACKET,
                "category.autototem"
        ));
        delayDownKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autototem.delay_down",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_BRACKET,
                "category.autototem"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(AutoTotemClient::onTick);

        HudRenderCallback.EVENT.register(AutoTotemClient::renderHud);
    }

    private static void onTick(MinecraftClient client) {
        while (toggleKey.wasPressed()) {
            config.enabled = !config.enabled;
            config.save();
            feedback(client, "AutoTotem: " + (config.enabled ? "ON" : "OFF"));
        }
        while (delayDownKey.wasPressed()) {
            config.delayTicks = Math.max(0, config.delayTicks - 1);
            config.save();
            feedback(client, "Delay: " + config.delayTicks + "t");
        }
        while (delayUpKey.wasPressed()) {
            config.delayTicks = Math.min(100, config.delayTicks + 1);
            config.save();
            feedback(client, "Delay: " + config.delayTicks + "t");
        }

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

        ItemStack offhand = client.player.getOffHandStack();
        if (offhand.isOf(Items.TOTEM_OF_UNDYING)) {
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

    private static void feedback(MinecraftClient client, String message) {
        if (client.player != null) {
            client.player.sendMessage(Text.literal(message), true);
        }
    }

    private static void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden || client.player == null) {
            return;
        }

        String label = "AutoTotem [" + config.delayTicks + "t]";
        int width = context.getScaledWindowWidth();
        int x = width - 5 - client.textRenderer.getWidth(label);
        int color = config.enabled ? 0xFF55FF55 : 0xFF555555;
        context.drawTextWithShadow(client.textRenderer, label, x, 5, color);
    }
}
