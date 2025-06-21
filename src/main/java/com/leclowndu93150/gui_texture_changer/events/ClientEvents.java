package com.leclowndu93150.gui_texture_changer.events;

import com.leclowndu93150.gui_texture_changer.GUITextureChanger;
import com.leclowndu93150.gui_texture_changer.commands.ScreenIdCommand;
import com.leclowndu93150.gui_texture_changer.config.GuiTextureConfigLoader;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mod.EventBusSubscriber(modid = GUITextureChanger.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ScreenIdCommand.register(event.getDispatcher());
    }

    public static void onResourceReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new GuiTextureResourceReloadListener());
    }

    public static class GuiTextureResourceReloadListener implements PreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager,
                                              ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
                                              Executor backgroundExecutor, Executor gameExecutor) {
            return CompletableFuture.runAsync(() -> {
                GuiTextureConfigLoader.loadConfigs(resourceManager);
            }, backgroundExecutor).thenCompose(preparationBarrier::wait);
        }
    }
}
