package com.nth.autototem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class AutoTotemScreen extends Screen {

    private final Screen parent;
    private boolean capturingKey = false;
    private ButtonWidget openKeyButton;

    public AutoTotemScreen(Screen parent) {
        super(Text.literal("Auto Totem"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(toggleText(), button -> {
            AutoTotemClient.getConfig().enabled = !AutoTotemClient.getConfig().enabled;
            AutoTotemClient.getConfig().save();
            button.setMessage(toggleText());
        }).dimensions(width / 2 - 100, 60, 200, 20).build());

        addDrawableChild(new DelaySlider(width / 2 - 100, 90, 200, 20));

        openKeyButton = addDrawableChild(ButtonWidget.builder(openKeyText(), button -> {
            capturingKey = true;
            button.setMessage(Text.literal("> Press a key..."));
        }).dimensions(width / 2 - 100, 120, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> close())
                .dimensions(width / 2 - 100, height - 30, 200, 20).build());
    }

    private static Text toggleText() {
        return Text.literal("Auto Totem: " + (AutoTotemClient.getConfig().enabled ? "ON" : "OFF"));
    }

    private static Text openKeyText() {
        return Text.literal("Open key: " + AutoTotemKeys.name());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (capturingKey && keyCode != org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            AutoTotemKeys.rebind(keyCode);
            capturingKey = false;
            openKeyButton.setMessage(openKeyText());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        AutoTotemClient.getConfig().save();
        if (client != null) {
            client.setScreen(parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 30, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    private class DelaySlider extends SliderWidget {

        DelaySlider(int x, int y, int width, int height) {
            super(x, y, width, height, Text.empty(), AutoTotemClient.getConfig().delayTicks / 100.0);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal("Delay: " + getDelay() + " ticks"));
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
