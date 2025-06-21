package com.leclowndu93150.gui_texture_changer.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.leclowndu93150.gui_texture_changer.GUITextureChanger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GuiTextureConfigLoader {
    private static final Map<String, GuiTextureConfig> screenConfigs = new HashMap<>();
    private static final Gson GSON = new Gson();

    public static void loadConfigs(ResourceManager resourceManager) {
        screenConfigs.clear();

        Collection<ResourceLocation> resources = resourceManager.listResources("guitextures",
                location -> location.getPath().endsWith(".json")).keySet();

        for (ResourceLocation resourceLocation : resources) {
            try {
                Resource resource = resourceManager.getResource(resourceLocation).orElse(null);
                if (resource == null) continue;

                try (InputStream inputStream = resource.open();
                     InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

                    JsonObject json = GSON.fromJson(reader, JsonObject.class);
                    GuiTextureConfig config = GuiTextureConfig.fromJson(json);
                    screenConfigs.put(config.getScreenId(), config);

                    GUITextureChanger.LOGGER.info("Loaded GUI texture config for screen: {} from {}",
                            config.getScreenId(), resourceLocation);
                }
            } catch (Exception e) {
                GUITextureChanger.LOGGER.error("Failed to load GUI texture config from {}", resourceLocation, e);
            }
        }

        GUITextureChanger.LOGGER.info("Loaded {} GUI texture configurations", screenConfigs.size());
    }

    public static GuiTextureConfig getConfigForScreen(String screenId) {
        return screenConfigs.get(screenId);
    }

    public static Set<String> getLoadedScreenIds() {
        return new HashSet<>(screenConfigs.keySet());
    }
}
