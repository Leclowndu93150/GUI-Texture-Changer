package com.leclowndu93150.gui_texture_changer;

import com.leclowndu93150.gui_texture_changer.events.ClientEvents;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(GUITextureChanger.MODID)
public class GUITextureChanger {

    public static final String MODID = "gui_texture_changer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GUITextureChanger() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ClientEvents::onResourceReload);
        }
    }

}
