package com.leclowndu93150.gui_texture_changer.util;

import com.leclowndu93150.gui_texture_changer.GUITextureChanger;
import com.leclowndu93150.gui_texture_changer.config.GuiTextureConfigLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ScreenTracker {
    private static final Set<String> discoveredScreens = new LinkedHashSet<>();
    private static final Map<String, Set<ResourceLocation>> screenTextures = new LinkedHashMap<>();
    private static boolean trackingEnabled = false;

    public static void startTracking() {
        trackingEnabled = true;
        discoveredScreens.clear();
        screenTextures.clear();
        sendChatMessage(Component.literal("Block GUI tracking started! Open containers to discover their block IDs.")
                .withStyle(ChatFormatting.GREEN));
        GUITextureChanger.LOGGER.info("Block GUI tracking started");
    }

    public static void stopTracking() {
        trackingEnabled = false;
        sendChatMessage(Component.literal("Block GUI tracking stopped.")
                .withStyle(ChatFormatting.YELLOW));
        GUITextureChanger.LOGGER.info("Block GUI tracking stopped");
    }

    public static void trackScreen(String blockId) {
        if (trackingEnabled && blockId != null && !discoveredScreens.contains(blockId)) {
            discoveredScreens.add(blockId);
            screenTextures.put(blockId, new LinkedHashSet<>());
            Set<String> loadedConfigs = GuiTextureConfigLoader.getLoadedScreenIds();
            boolean isConfigured = loadedConfigs.contains(blockId);

            MutableComponent message = Component.literal("Discovered: ").withStyle(ChatFormatting.AQUA);

            if (blockId.contains(":")) {
                String[] parts = blockId.split(":", 2);
                message.append(Component.literal(parts[0]).withStyle(ChatFormatting.AQUA));
                message.append(Component.literal(":").withStyle(ChatFormatting.GRAY));
                message.append(Component.literal(parts[1]).withStyle(ChatFormatting.YELLOW));
            } else {
                message.append(Component.literal(blockId).withStyle(ChatFormatting.YELLOW));
            }

            if (isConfigured) {
                message.append(Component.literal(" (CONFIGURED)").withStyle(ChatFormatting.GREEN));
            }

            sendChatMessage(message);
            GUITextureChanger.LOGGER.info("Discovered block GUI: {}{}", blockId, isConfigured ? " (configured)" : "");
        }
    }

    public static void trackTexture(String blockId, ResourceLocation texture) {
        if (trackingEnabled && blockId != null && texture != null) {
            Set<ResourceLocation> textures = screenTextures.computeIfAbsent(blockId, k -> new LinkedHashSet<>());

            if (!textures.contains(texture)) {
                textures.add(texture);

                MutableComponent message = Component.literal("  Texture: ").withStyle(ChatFormatting.GRAY);
                message.append(Component.literal(texture.toString()).withStyle(ChatFormatting.WHITE));

                sendChatMessage(message);
                GUITextureChanger.LOGGER.info("Block '{}' uses texture: {}", blockId, texture);
            }
        }
    }

    public static Set<String> getDiscoveredScreens() {
        return new LinkedHashSet<>(discoveredScreens);
    }

    public static Map<String, Set<ResourceLocation>> getScreenTextures() {
        return new LinkedHashMap<>(screenTextures);
    }

    public static boolean isTracking() {
        return trackingEnabled;
    }

    public static void clearDiscovered() {
        discoveredScreens.clear();
        screenTextures.clear();
    }

    private static void sendChatMessage(Component message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(message);
        }
    }
}