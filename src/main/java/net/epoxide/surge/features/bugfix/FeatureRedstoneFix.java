package net.epoxide.surge.features.bugfix;

import java.util.WeakHashMap;

import net.epoxide.surge.features.Feature;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraftforge.common.config.Configuration;

/**
 * Fixes MC-101233 which is a memory leak in the Redstone Torch class. Mojang currently stores
 * the toggle state of all redstone torches contained within a given world in a static HashMap.
 * This HashMap uses a world as a key, however this creates a strong reference to the world and
 * prevents it from being garbage collected. The result of this is that for every world joined,
 * a small amount of system memory becomes dedicated to that world, and can not be used for
 * other things until the game is restarted. By switching to a WeakHashMap the world can be
 * garbage collected, allowing the previously restricted memory to be used elsewhere.
 */
public class FeatureRedstoneFix extends Feature {
    
    private static boolean enabled = true;
    
    @Override
    public void onPreInit () {
        
        if (enabled)
            BlockRedstoneTorch.toggles = new WeakHashMap<>();
    }
    
    @Override
    public void setupConfig (Configuration config) {
        
        enabled = config.getBoolean("Redstone Torch Memory Leak", "memleaks", true, "Fixes a memory leak with the redstone torch. bugs.mojang.com/browse/MC-101233");
    }
}