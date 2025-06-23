package com.leclowndu93150.gui_texture_changer.mixin;

import com.leclowndu93150.gui_texture_changer.util.TextureChanger;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

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
}