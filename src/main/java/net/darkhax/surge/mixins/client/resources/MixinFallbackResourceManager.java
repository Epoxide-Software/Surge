package net.darkhax.surge.mixins.client.resources;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.darkhax.surge.lib.ICheckableResourceManager;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

@Mixin(FallbackResourceManager.class)
public class MixinFallbackResourceManager implements ICheckableResourceManager {

    @Shadow
    protected List<IResourcePack> resourcePacks;

    @Override
    public boolean hasResource (ResourceLocation location) {

        return this.resourcePacks.stream().anyMatch(pack -> pack.resourceExists(location));
    }
}