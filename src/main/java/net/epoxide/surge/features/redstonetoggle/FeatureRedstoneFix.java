package net.epoxide.surge.features.redstonetoggle;

import java.util.WeakHashMap;

import net.epoxide.surge.features.Feature;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Fixes MC-101233 which is a memory leak in the Redstone Torch class. Mojang currently stores
 * the toggle state of all redstone torches contained within a given world in a static HashMap.
 * This HashMap uses a world as a key, however this creates a strong reference to the world and
 * prevents it from being garbage collected. The result of this is that for every world joined,
 * a small amount of system memory becomes dedicated to that world, and can not be used for
 * other things until the game is restarted. By switching to a WeakHashMap the world can be
 * garbage collected, allowing the previously restricted memory to be used elsewhere.
 */
@SideOnly(Side.CLIENT)
public class FeatureRedstoneFix extends Feature {
    
    @Override
    public void onPreInit () {
        
        if (this.enabled)
            BlockRedstoneTorch.toggles = new WeakHashMap<>();
    }
}