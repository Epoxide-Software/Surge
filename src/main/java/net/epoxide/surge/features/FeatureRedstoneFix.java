package net.epoxide.surge.features;

import java.util.List;

import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public class FeatureRedstoneFix extends Feature {
    
    private static boolean enabled = true;
    
    @Override
    public void onPreInit () {
        
        if (enabled)
            BlockRedstoneTorch.toggles = new java.util.WeakHashMap<World, List<BlockRedstoneTorch.Toggle>>();
    }
    
    @Override
    public void setupConfig (Configuration config) {
        
        enabled = config.getBoolean("Redstone Torch Memory Leak", "memleaks", true, "Fixes a memory leak with the redstone torch. bugs.mojang.com/browse/MC-101233");
    }
}