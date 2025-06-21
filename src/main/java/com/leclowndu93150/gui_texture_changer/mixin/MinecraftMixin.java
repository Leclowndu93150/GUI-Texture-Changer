package com.leclowndu93150.gui_texture_changer.mixin;

import com.leclowndu93150.gui_texture_changer.util.TextureChanger;
import com.leclowndu93150.gui_texture_changer.util.ScreenTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("TAIL"))
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen == null) {
            TextureChanger.clearCurrentScreen();
        } else {
            String screenId = getScreenId(screen);
            TextureChanger.setCurrentScreen(screenId);
            ScreenTracker.trackScreen(screenId);
        }
    }

    @Inject(method = "runTick(Z)V", at = @At("HEAD"))
    private void onTickStart(boolean p_91384_, CallbackInfo ci) {
        TextureChanger.setRenderingScreen(false);
    }

    @Inject(method = "runTick(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V"))
    private void onScreenRenderStart(boolean p_91384_, CallbackInfo ci) {
        TextureChanger.setRenderingScreen(true);
    }

    @Inject(method = "runTick(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V", shift = At.Shift.AFTER))
    private void onScreenRenderEnd(boolean p_91384_, CallbackInfo ci) {
        TextureChanger.setRenderingScreen(false);
    }

    private String getScreenId(Screen screen) {
        String modId = getModIdFromPackage(screen.getClass());
        String className = screen.getClass().getSimpleName();

        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            AbstractContainerMenu menu = containerScreen.getMenu();
            if (menu != null) {
                String menuModId = getModIdFromPackage(menu.getClass());
                String menuName = menu.getClass().getSimpleName().replace("Menu", "").toLowerCase();
                return menuModId + ":" + menuName;
            }
        }

        if (className.endsWith("Screen")) {
            className = className.substring(0, className.length() - 6);
        }

        return modId + ":" + className.toLowerCase();
    }

    private String getModIdFromPackage(Class<?> clazz) {
        String packageName = clazz.getPackageName();

        if (packageName.startsWith("net.minecraft")) {
            return "minecraft";
        }

        String[] parts = packageName.split("\\.");
        if (parts.length >= 2) {
            return parts[parts.length - 1];
        }

        return "minecraft";
    }
}