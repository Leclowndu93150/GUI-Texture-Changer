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
        dispatcher.register(Commands.literal("blockgui")
                .then(Commands.literal("start")
                        .executes(ScreenIdCommand::startTracking))
                .then(Commands.literal("stop")
                        .executes(ScreenIdCommand::stopTracking))
                .then(Commands.literal("list")
                        .executes(ScreenIdCommand::listDiscoveredBlocks))
                .then(Commands.literal("clear")
                        .executes(ScreenIdCommand::clearDiscovered))
                .then(Commands.literal("configs")
                        .executes(ScreenIdCommand::listLoadedConfigs))
                .executes(ScreenIdCommand::showStatus)
        );

        // Keep the old command for compatibility
        dispatcher.register(Commands.literal("screenid")
                .then(Commands.literal("start")
                        .executes(ScreenIdCommand::startTracking))
                .then(Commands.literal("stop")
                        .executes(ScreenIdCommand::stopTracking))
                .then(Commands.literal("list")
                        .executes(ScreenIdCommand::listDiscoveredBlocks))
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
                        Component.literal("Block GUI tracking started! Open containers to discover their block IDs.").withStyle(ChatFormatting.GREEN),
                false);
        return Command.SINGLE_SUCCESS;
    }

    private static int stopTracking(CommandContext<CommandSourceStack> context) {
        ScreenTracker.stopTracking();
        context.getSource().sendSuccess(() ->
                        Component.literal("Block GUI tracking stopped.").withStyle(ChatFormatting.YELLOW),
                false);
        return Command.SINGLE_SUCCESS;
    }

    private static int showStatus(CommandContext<CommandSourceStack> context) {
        if (ScreenTracker.isTracking()) {
            context.getSource().sendSuccess(() ->
                            Component.literal("Block GUI tracking is ").withStyle(ChatFormatting.WHITE)
                                    .append(Component.literal("ACTIVE").withStyle(ChatFormatting.GREEN))
                                    .append(Component.literal(". Use '/blockgui list' to see discovered blocks.").withStyle(ChatFormatting.WHITE)),
                    false);
        } else {
            context.getSource().sendSuccess(() ->
                            Component.literal("Block GUI tracking is ").withStyle(ChatFormatting.WHITE)
                                    .append(Component.literal("INACTIVE").withStyle(ChatFormatting.RED))
                                    .append(Component.literal(". Use '/blockgui start' to begin tracking.").withStyle(ChatFormatting.WHITE)),
                    false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int listDiscoveredBlocks(CommandContext<CommandSourceStack> context) {
        Set<String> discoveredBlocks = ScreenTracker.getDiscoveredScreens();
        Map<String, Set<ResourceLocation>> blockTextures = ScreenTracker.getScreenTextures();
        Set<String> loadedConfigs = GuiTextureConfigLoader.getLoadedScreenIds();

        if (discoveredBlocks.isEmpty()) {
            context.getSource().sendSuccess(() ->
                            Component.literal("No block GUIs discovered yet. Start tracking with '/blockgui start' and open some containers!").withStyle(ChatFormatting.YELLOW),
                    false);
        } else {
            context.getSource().sendSuccess(() -> {
                MutableComponent message = Component.literal("Discovered block containers (" + discoveredBlocks.size() + "):").withStyle(ChatFormatting.GREEN);

                for (String blockId : discoveredBlocks) {
                    boolean isConfigured = loadedConfigs.contains(blockId);
                    message.append(Component.literal("\n\n").withStyle(ChatFormatting.GRAY));
                    message.append(Component.literal("Block: ").withStyle(ChatFormatting.WHITE));

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

                    Set<ResourceLocation> textures = blockTextures.get(blockId);
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
                        Component.literal("Cleared discovered blocks list.").withStyle(ChatFormatting.YELLOW),
                false);
        return Command.SINGLE_SUCCESS;
    }

    private static int listLoadedConfigs(CommandContext<CommandSourceStack> context) {
        Set<String> loadedBlockIds = GuiTextureConfigLoader.getLoadedScreenIds();

        if (loadedBlockIds.isEmpty()) {
            context.getSource().sendSuccess(() ->
                            Component.literal("No block GUI texture configurations loaded").withStyle(ChatFormatting.RED),
                    false);
        } else {
            context.getSource().sendSuccess(() -> {
                MutableComponent message = Component.literal("Loaded block configurations (" + loadedBlockIds.size() + "):").withStyle(ChatFormatting.GREEN);

                for (String blockId : loadedBlockIds) {
                    message.append(Component.literal("\n  - ").withStyle(ChatFormatting.GRAY));

                    if (blockId.contains(":")) {
                        String[] parts = blockId.split(":", 2);
                        message.append(Component.literal(parts[0]).withStyle(ChatFormatting.AQUA));
                        message.append(Component.literal(":").withStyle(ChatFormatting.GRAY));
                        message.append(Component.literal(parts[1]).withStyle(ChatFormatting.YELLOW));
                    } else {
                        message.append(Component.literal(blockId).withStyle(ChatFormatting.YELLOW));
                    }
                }

                return message;
            }, false);
        }

        return Command.SINGLE_SUCCESS;
    }
}