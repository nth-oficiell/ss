package com.nth.autototem;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AutoTotemScreen extends Screen {

    private final Screen parent;
    private boolean capturingKey = false;
    private Button openKeyButton;

    public AutoTotemScreen(Screen parent) {
        super(Component.literal("Auto Totem"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(toggleText(), button -> {
            AutoTotemClient.getConfig().enabled = !AutoTotemClient.getConfig().enabled;
            AutoTotemClient.getConfig().save();
            button.setMessage(toggleText());
        }).bounds(width / 2 - 100, 60, 200, 20).build());

        addRenderableWidget(new DelaySlider(width / 2 - 100, 90, 200, 20));

        openKeyButton = addRenderableWidget(Button.builder(openKeyText(), button -> {
            capturingKey = true;
            button.setMessage(Component.literal("> Press a key..."));
        }).bounds(width / 2 - 100, 120, 200, 20).build());

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(width / 2 - 100, height - 30, 200, 20).build());
    }

    private static Component toggleText() {
        return Component.literal("Auto Totem: " + (AutoTotemClient.getConfig().enabled ? "ON" : "OFF"));
    }

    private static Component openKeyText() {
        return Component.literal("Open key: " + AutoTotemKeys.name());
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (capturingKey && input.key() != 256) {
            AutoTotemKeys.rebind(input.key());
            capturingKey = false;
            openKeyButton.setMessage(openKeyText());
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void onClose() {
        AutoTotemClient.getConfig().save();
        if (minecraft != null) {
            minecraft.setScreenAndShow(parent);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(font, title, width / 2, 30, 0xFFFFFF);
    }

    private class DelaySlider extends AbstractSliderButton {

        DelaySlider(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty(), AutoTotemClient.getConfig().delayTicks / 100.0);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal("Delay: " + getDelay() + " ticks"));
        }

        @Override
        protected void applyValue() {
            AutoTotemClient.getConfig().delayTicks = getDelay();
            AutoTotemClient.getConfig().save();
        }

        private int getDelay() {
            return (int) Math.round(value * 100);
        }
    }
}
