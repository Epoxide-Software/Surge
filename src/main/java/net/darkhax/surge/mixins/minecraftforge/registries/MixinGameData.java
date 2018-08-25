package net.darkhax.surge.mixins.minecraftforge.registries;

import java.util.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.registries.GameData;

@Mixin(GameData.class)
public class MixinGameData {

    @Inject(method = "checkPrefix(Ljava/lang/String;)Lnet/minecraft/util/ResourceLocation;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void checkPrefix (String name, CallbackInfoReturnable<ResourceLocation> info) {

        final int seperator = name.lastIndexOf(":");
        String namespace = seperator == -1 ? "" : name.substring(0, seperator).toLowerCase(Locale.ROOT);
        ;
        final String path = seperator == -1 ? name : name.substring(seperator + 1);

        if (namespace.isEmpty()) {

            final ModContainer activeMod = Loader.instance().activeModContainer();

            if (activeMod != null) {

                namespace = activeMod instanceof InjectedModContainer && ((InjectedModContainer) activeMod).wrappedContainer instanceof FMLContainer ? "minecraft" : activeMod.getModId().toLowerCase(Locale.ROOT);
            }
        }

        info.setReturnValue(new ResourceLocation(namespace, path));
    }
}