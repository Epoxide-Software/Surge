package net.darkhax.surge.mixins.client.resources;

import net.minecraft.util.ResourceLocation;

public interface ICheckableResourceManager {

    boolean hasResource (ResourceLocation location);
}