package net.epoxide.surge.features;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Feature {
    
    /**
     * Whether or not this feature is enabled.
     */
    protected boolean enabled;
    
    /**
     * The category name used for this feature in the config file.
     */
    protected String configName;
    
    /**
     * Called when the mod enters the preInit phase of loading. This is after
     * {@link #setupConfiguration(Configuration)} but before {@link #onClientPreInit()}.
     */
    public void onPreInit () {
    
    }
    
    /**
     * Called when the mod enters the init phase of loading.
     */
    public void onInit () {
    
    }
    
    /**
     * Called when the mod enters the postInit phase of loading.
     */
    public void onPostInit () {
    
    }
    
    /**
     * Called when FML has finished loading mods.
     */
    public void onFMLFinished () {
    
    }
    
    /**
     * Called before {@link #onPreInit()}. Allows for configuration options to be
     * detected/generated. A feature being enabled or not is handled automatically by the
     * feature manager.
     * 
     * @param config The configuration object to pull data from.
     */
    public void setupConfig (Configuration config) {
    
    }
    
    /**
     * Checks if the feature should be enabled by default.
     * 
     * @return Whether or not the feature should be enabled by default.
     */
    public boolean enabledByDefault () {
        
        return true;
    }
    
    /**
     * Gives the feature access to transforming class byes.
     * 
     * @param name The initial name.
     * @param transformedName The transformed name.
     * @param bytes The original bytes of the class.
     * @return The new bytes for the class.
     */
    public byte[] transform (String name, String transformedName, byte[] bytes) {
        
        return bytes;
    }
    
    /**
     * Called while the mod is in the client side preInit phase.
     */
    @SideOnly(Side.CLIENT)
    public void onClientPreInit () {
    
    }
    
    /**
     * Called while the mod is in the client side init phase.
     */
    @SideOnly(Side.CLIENT)
    public void onClientInit () {
    
    }
    
    /**
     * Called while the mod is in the client side postInit phase.
     */
    @SideOnly(Side.CLIENT)
    public void onClientPostInit () {
    
    }

    public boolean isEnabled () {
        return enabled;
    }
}