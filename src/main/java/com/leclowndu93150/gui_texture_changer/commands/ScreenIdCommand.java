package com.leclowndu93150.gui_texture_changer.commands;

import com.leclowndu93150.gui_texture_changer.config.GuiTextureConfigLoader;
import com.leclowndu93150.gui_texture_changer.util.ScreenTracker;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;

public class ScreenIdCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("screenid")
                .then(Commands.literal("start")
                        .executes(ScreenIdCommand::startTracking))
                .then(Commands.literal("stop")
                        .executes(ScreenIdCommand::stopTracking))
                .then(Commands.literal("list")
                        .executes(ScreenIdCommand::listDiscoveredScreens))
                .then(Commands.literal("clear")
                        .executes(ScreenIdCommand::clearDiscovered))
                .then(Commands.literal("configs")
                        .executes(ScreenIdCommand::listLoadedConfigs))
                .executes(ScreenIdCommand::showStatus)
        );
    }

    private static int startTracking(CommandContext<CommandSourceStack> context) {
        ScreenTracker.startTracking();
        context.getSource().sendSuccess(() ->
                        Component.literal("Screen tracking started! Open GUIs to discover their IDs.").withStyle(ChatFormatting.GREEN),
                false);
        return Command.SINGLE_SUCCESS;
    }

    private static int stopTracking(CommandContext<CommandSourceStack> context) {
        ScreenTracker.stopTracking();
        context.getSource().sendSuccess(() ->
                        Component.literal("Screen tracking stopped.").withStyle(ChatFormatting.YELLOW),
                false);
        return Command.SINGLE_SUCCESS;
    }

    private static int showStatus(CommandContext<CommandSourceStack> context) {
        if (ScreenTracker.isTracking()) {
            context.getSource().sendSuccess(() ->
                            Component.literal("Screen tracking is ").withStyle(ChatFormatting.WHITE)
                                    .append(Component.literal("ACTIVE").withStyle(ChatFormatting.GREEN))
                                    .append(Component.literal(". Use '/screenid list' to see discovered screens.").withStyle(ChatFormatting.WHITE)),
                    false);
        } else {
            context.getSource().sendSuccess(() ->
                            Component.literal("Screen tracking is ").withStyle(ChatFormatting.WHITE)
                                    .append(Component.literal("INACTIVE").withStyle(ChatFormatting.RED))
                                    .append(Component.literal(". Use '/screenid start' to begin tracking.").withStyle(ChatFormatting.WHITE)),
                    false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int listDiscoveredScreens(CommandContext<CommandSourceStack> context) {
        Set<String> discoveredScreens = ScreenTracker.getDiscoveredScreens();
        Map<String, Set<ResourceLocation>> screenTextures = ScreenTracker.getScreenTextures();
        Set<String> loadedConfigs = GuiTextureConfigLoader.getLoadedScreenIds();

        if (discoveredScreens.isEmpty()) {
            context.getSource().sendSuccess(() ->
                            Component.literal("No screens discovered yet. Start tracking with '/screenid start' and open some GUIs!").withStyle(ChatFormatting.YELLOW),
                    false);
        } else {
            context.getSource().sendSuccess(() -> {
                MutableComponent message = Component.literal("Discovered screens (" + discoveredScreens.size() + "):").withStyle(ChatFormatting.GREEN);

                for (String screenId : discoveredScreens) {
                    boolean isConfigured = loadedConfigs.contains(screenId);
                    message.append(Component.literal("\n\n").withStyle(ChatFormatting.GRAY));
                    message.append(Component.literal("Screen: ").withStyle(ChatFormatting.WHITE));

                    if (screenId.contains(":")) {
                        String[] parts = screenId.split(":", 2);
                        message.append(Component.literal(parts[0]).withStyle(ChatFormatting.AQUA));
                        message.append(Component.literal(":").withStyle(ChatFormatting.GRAY));
                        message.append(Component.literal(parts[1]).withStyle(ChatFormatting.YELLOW));
                    } else {
                        message.append(Component.literal(screenId).withStyle(ChatFormatting.YELLOW));
                    }

                    if (isConfigured) {
                        message.append(Component.literal(" (CONFIGURED)").withStyle(ChatFormatting.GREEN));
                    }

                    Set<ResourceLocation> textures = screenTextures.get(screenId);
                    if (textures != null && !textures.isEmpty()) {
                        message.append(Component.literal("\n  Textures:").withStyle(ChatFormatting.GRAY));
                        for (ResourceLocation texture : textures) {
                            message.append(Component.literal("\n    - ").withStyle(ChatFormatting.DARK_GRAY));
                            message.append(Component.literal(texture.toString()).withStyle(ChatFormatting.WHITE));
                        }
                    }
                }

                return message;
            }, false);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int clearDiscovered(CommandContext<CommandSourceStack> context) {
        ScreenTracker.clearDiscovered();
        context.getSource().sendSuccess(() ->
                        Component.literal("Cleared discovered screens list.").withStyle(ChatFormatting.YELLOW),
                false);
        return Command.SINGLE_SUCCESS;
    }

    private static int listLoadedConfigs(CommandContext<CommandSourceStack> context) {
        Set<String> loadedScreenIds = GuiTextureConfigLoader.getLoadedScreenIds();

        if (loadedScreenIds.isEmpty()) {
            context.getSource().sendSuccess(() ->
                            Component.literal("No GUI texture configurations loaded").withStyle(ChatFormatting.RED),
                    false);
        } else {
            context.getSource().sendSuccess(() -> {
                MutableComponent message = Component.literal("Loaded configurations (" + loadedScreenIds.size() + "):").withStyle(ChatFormatting.GREEN);

                for (String screenId : loadedScreenIds) {
                    message.append(Component.literal("\n  - ").withStyle(ChatFormatting.GRAY));

                    if (screenId.contains(":")) {
                        String[] parts = screenId.split(":", 2);
                        message.append(Component.literal(parts[0]).withStyle(ChatFormatting.AQUA));
                        message.append(Component.literal(":").withStyle(ChatFormatting.GRAY));
                        message.append(Component.literal(parts[1]).withStyle(ChatFormatting.YELLOW));
                    } else {
                        message.append(Component.literal(screenId).withStyle(ChatFormatting.YELLOW));
                    }
                }

                return message;
            }, false);
        }

        return Command.SINGLE_SUCCESS;
    }
}