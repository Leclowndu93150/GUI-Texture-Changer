package com.leclowndu93150.gui_texture_changer.util;

import com.leclowndu93150.gui_texture_changer.GUITextureChanger;
import com.leclowndu93150.gui_texture_changer.config.GuiTextureConfig;
import com.leclowndu93150.gui_texture_changer.config.GuiTextureConfigLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TextureChanger {
    private static final Map<ResourceLocation, ResourceLocation> textureReplacements = new HashMap<>();
    private static final Map<ResourceLocation, ResourceLocation> spriteReplacements = new HashMap<>();
    private static String currentBlockId = null;
    private static boolean renderingScreen = false;
    private static boolean hideTitle = false;

    public static ResourceLocation changeTexture(ResourceLocation texture) {
        if (!renderingScreen || currentBlockId == null) return texture;

        ScreenTracker.trackTexture(currentBlockId, texture);

        ResourceLocation replacement = textureReplacements.get(texture);
        return replacement != null ? replacement : texture;
    }

    public static ResourceLocation changeSprite(ResourceLocation sprite) {
        if (!renderingScreen || currentBlockId == null) return sprite;

        ResourceLocation replacement = spriteReplacements.get(sprite);
        return replacement != null ? replacement : sprite;
    }

    public static void setCurrentScreen(String blockId) {
        if (!Objects.equals(currentBlockId, blockId)) {
            currentBlockId = blockId;
            loadTexturesForBlock(blockId);
        }
    }

    public static String getCurrentScreenId() {
        return currentBlockId;
    }

    public static void setRenderingScreen(boolean rendering) {
        renderingScreen = rendering;
    }

    public static boolean shouldHideTitle() {
        return hideTitle && renderingScreen && currentBlockId != null;
    }

    public static void clearCurrentScreen() {
        currentBlockId = null;
        textureReplacements.clear();
        spriteReplacements.clear();
        hideTitle = false;
    }

    private static void loadTexturesForBlock(String blockId) {
        textureReplacements.clear();
        spriteReplacements.clear();
        hideTitle = false;

        GuiTextureConfig config = GuiTextureConfigLoader.getConfigForScreen(blockId);
        if (config != null) {
            textureReplacements.putAll(config.getTextureReplacements());
            spriteReplacements.putAll(config.getSpriteReplacements());
            hideTitle = config.shouldHideTitle();
            GUITextureChanger.LOGGER.info("Loaded {} texture replacements and {} sprite replacements for block: {} (hide title: {})",
                    textureReplacements.size(), spriteReplacements.size(), blockId, hideTitle);
        }
    }
}