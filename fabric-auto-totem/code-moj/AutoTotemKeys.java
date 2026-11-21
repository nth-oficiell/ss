package com.nth.autototem;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public final class AutoTotemKeys {

    private static final int DEFAULT_KEY = 79;

    private AutoTotemKeys() {
    }

    private static KeyMapping menuKey;

    public static void register() {
        menuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.autototem.open",
                InputConstants.Type.KEYBOARD,
                DEFAULT_KEY,
                KeyMapping.Category.MISC
        ));
    }

    public static boolean wasPressed() {
        return menuKey.consumeClick();
    }

    public static String name() {
        return menuKey.getTranslatedKeyMessage().getString();
    }

    public static void rebind(int keyCode) {
        menuKey.setKey(InputConstants.Type.KEYBOARD.getOrCreate(keyCode));
        KeyMapping.resetMapping();
    }
}
