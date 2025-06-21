package com.leclowndu93150.gui_texture_changer.mixin;

import com.leclowndu93150.gui_texture_changer.util.TextureChanger;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = GuiGraphics.class, priority = 800)
public abstract class GuiGraphicsMixin {

    @ModifyVariable(
            method = "blit(Lnet/minecraft/resources/ResourceLocation;IIIIIIIFFII)V",
            at = @At("HEAD"),
            index = 1,
            argsOnly = true
    )
    private ResourceLocation changeTexture(ResourceLocation texture) {
        return texture != null ? TextureChanger.changeTexture(texture) : null;
    }
}
