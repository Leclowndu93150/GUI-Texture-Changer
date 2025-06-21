package com.leclowndu93150.gui_texture_changer.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.HashMap;
import java.util.Map;

public class GuiTextureConfig {
    private final String screenId;
    private final Map<ResourceLocation, ResourceLocation> textureReplacements;
    private final Map<ResourceLocation, ResourceLocation> spriteReplacements;
    private final boolean hideTitle;

    public GuiTextureConfig(String screenId, Map<ResourceLocation, ResourceLocation> textureReplacements,
                            Map<ResourceLocation, ResourceLocation> spriteReplacements, boolean hideTitle) {
        this.screenId = screenId;
        this.textureReplacements = new HashMap<>(textureReplacements);
        this.spriteReplacements = new HashMap<>(spriteReplacements);
        this.hideTitle = hideTitle;
    }

    public String getScreenId() {
        return screenId;
    }

    public Map<ResourceLocation, ResourceLocation> getTextureReplacements() {
        return textureReplacements;
    }

    public Map<ResourceLocation, ResourceLocation> getSpriteReplacements() {
        return spriteReplacements;
    }

    public boolean shouldHideTitle() {
        return hideTitle;
    }

    public static GuiTextureConfig fromJson(JsonObject json) {
        String screenId = GsonHelper.getAsString(json, "screen_id");

        Map<ResourceLocation, ResourceLocation> textureReplacements = new HashMap<>();
        if (json.has("textures")) {
            JsonObject textures = GsonHelper.getAsJsonObject(json, "textures");
            for (Map.Entry<String, JsonElement> entry : textures.entrySet()) {
                ResourceLocation original = new ResourceLocation(entry.getKey());
                ResourceLocation replacement = new ResourceLocation(entry.getValue().getAsString());
                textureReplacements.put(original, replacement);
            }
        }

        Map<ResourceLocation, ResourceLocation> spriteReplacements = new HashMap<>();
        if (json.has("sprites")) {
            JsonObject sprites = GsonHelper.getAsJsonObject(json, "sprites");
            for (Map.Entry<String, JsonElement> entry : sprites.entrySet()) {
                ResourceLocation original = new ResourceLocation(entry.getKey());
                ResourceLocation replacement = new ResourceLocation(entry.getValue().getAsString());
                spriteReplacements.put(original, replacement);
            }
        }

        boolean hideTitle = GsonHelper.getAsBoolean(json, "hide_title", false);

        return new GuiTextureConfig(screenId, textureReplacements, spriteReplacements, hideTitle);
    }
}