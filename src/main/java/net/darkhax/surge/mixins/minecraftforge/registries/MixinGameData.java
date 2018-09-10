package net.darkhax.surge.mixins.minecraftforge.registries;

import java.util.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.darkhax.surge.core.SurgeConfiguration;
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

        // Check if prefix is enabled.
        if (SurgeConfiguration.fastPrefixChecking) {

            // Get position of the last separator
            final int separator = name.lastIndexOf(':');

            // Resolve the namespace
            String namespace = separator == -1 ? "" : name.substring(0, separator).toLowerCase(Locale.ROOT);

            // Resolve the path
            final String path = separator == -1 ? name : name.substring(separator + 1);

            // If there is no namespace, try to get it from the active mod.
            if (namespace.isEmpty()) {

                final ModContainer activeMod = Loader.instance().activeModContainer();

                if (activeMod != null) {

                    namespace = activeMod instanceof InjectedModContainer && ((InjectedModContainer) activeMod).wrappedContainer instanceof FMLContainer ? "minecraft" : activeMod.getModId().toLowerCase(Locale.ROOT);
                }
            }

            info.setReturnValue(new ResourceLocation(namespace, path));
        }
    }
}