package com.leclowndu93150.gui_texture_changer.events;

import com.leclowndu93150.gui_texture_changer.GUITextureChanger;
import com.leclowndu93150.gui_texture_changer.util.TextureChanger;
import com.leclowndu93150.gui_texture_changer.util.ScreenTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = GUITextureChanger.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeEventHandler {

    private static BlockPos lastInteractedPos = null;
    private static Level lastInteractedLevel = null;
    private static long lastInteractionTime = 0;
    private static final long INTERACTION_TIMEOUT = 500; // 500ms timeout

    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide() && event.getEntity() == Minecraft.getInstance().player) {
            lastInteractedPos = event.getPos();
            lastInteractedLevel = event.getLevel();
            lastInteractionTime = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public static void onEntityRightClick(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide() && event.getEntity() == Minecraft.getInstance().player) {
            // Clear block position when interacting with entities to avoid false associations
            lastInteractedPos = null;
            lastInteractedLevel = null;
            lastInteractionTime = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }

        String blockId = null;
        AbstractContainerMenu menu = containerScreen.getMenu();

        // Special case for merchants
        if (menu instanceof MerchantMenu merchantMenu) {
            try {
                Field traderField = MerchantMenu.class.getDeclaredField("trader");
                traderField.setAccessible(true);
                Object trader = traderField.get(merchantMenu);
                if (trader instanceof AbstractVillager villager) {
                    ResourceLocation villagerType = BuiltInRegistries.ENTITY_TYPE.getKey(villager.getType());
                    blockId = villagerType.toString();
                }
            } catch (Exception e) {
                blockId = "minecraft:merchant";
            }
        }
        // For all other containers, use the block position from recent right-click
        else if (lastInteractedPos != null && lastInteractedLevel != null
                && System.currentTimeMillis() - lastInteractionTime < INTERACTION_TIMEOUT) {
            BlockState blockState = lastInteractedLevel.getBlockState(lastInteractedPos);
            ResourceLocation blockResourceLocation = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());
            blockId = blockResourceLocation.toString();
        }

        if (blockId != null) {
            TextureChanger.setCurrentScreen(blockId);
            ScreenTracker.trackScreen(blockId);
        }
    }

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        Screen screen = event.getScreen();
        if (screen instanceof AbstractContainerScreen<?>) {
            TextureChanger.clearCurrentScreen();
        }
    }
}