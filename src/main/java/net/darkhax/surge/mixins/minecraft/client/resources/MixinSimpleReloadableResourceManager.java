package net.darkhax.surge.mixins.minecraft.client.resources;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.darkhax.surge.lib.ICheckableResourceManager;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

@Mixin(SimpleReloadableResourceManager.class)
public class MixinSimpleReloadableResourceManager implements ICheckableResourceManager {

    @Shadow
    private Map<String, FallbackResourceManager> domainResourceManagers;

    @Override
    public boolean hasResource (ResourceLocation location) {

        final FallbackResourceManager manager = this.domainResourceManagers.get(location.getNamespace());
        return manager instanceof ICheckableResourceManager && ((ICheckableResourceManager) manager).hasResource(location);
    }
}