package net.darkhax.surge.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.darkhax.surge.mixins.client.resources.ICheckableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;

@Mixin(ModelBlockAnimation.class)
public class MixinModelBlockAnimation {

    @Shadow(remap = false)
    private static ModelBlockAnimation defaultModelBlockAnimation;

    @Inject(method = "loadVanillaAnimation(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/client/model/animation/ModelBlockAnimation;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void loadVanillaAnimation (IResourceManager manager, ResourceLocation location, CallbackInfoReturnable<ModelBlockAnimation> info) {

        if (manager instanceof ICheckableResourceManager && !((ICheckableResourceManager) manager).hasResource(location))
            info.setReturnValue(defaultModelBlockAnimation);
    }
}