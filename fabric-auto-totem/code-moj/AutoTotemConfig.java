package com.nth.autototem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AutoTotemConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("autototem.json");

    public boolean enabled = true;
    public int delayTicks = 0;

    public static AutoTotemConfig load() {
        if (!Files.exists(PATH)) {
            AutoTotemConfig config = new AutoTotemConfig();
            config.save();
            return config;
        }
        try {
            return GSON.fromJson(Files.readString(PATH), AutoTotemConfig.class);
        } catch (Exception e) {
            return new AutoTotemConfig();
        }
    }

    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(this));
        } catch (IOException ignored) {
        }
    }
}
