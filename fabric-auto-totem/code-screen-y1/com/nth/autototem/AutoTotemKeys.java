package com.nth.autototem;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class AutoTotemKeys {

    private AutoTotemKeys() {
    }

    private static KeyBinding menuKey;

    public static void register() {
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autototem.open",
                InputUtil.Type.KEYSYM,
                org.lwjgl.glfw.GLFW.GLFW_KEY_O,
                "category.autototem"
        ));
    }

    public static boolean wasPressed() {
        return menuKey.wasPressed();
    }

    public static String name() {
        return menuKey.getBoundKeyLocalizedText().getString();
    }

    public static void rebind(int keyCode) {
        menuKey.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(keyCode));
        KeyBinding.updateKeysByCode();
    }
}
