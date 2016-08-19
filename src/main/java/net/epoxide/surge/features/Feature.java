package net.epoxide.surge.features;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

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
     * Checks if the feature listens to any events.
     * 
     * @return Whether or not the feature listends to events.
     */
    public boolean usesEvents () {
        
        return false;
    }
    
    /**
     * Checks if the feature uses any ASM.
     * 
     * @return Whether or not the feature uses ASM.
     */
    public boolean isTransformer () {
        
        return false;
    }
    
    /**
     * Checks if the feature wants to transform a specific class.
     * 
     * @param name The name of the class being transformed.
     * @return Whether or not the feature wants to transform the class.
     */
    public boolean shouldTransform (String name) {
        
        return false;
    }
    
    /**
     * Allows features to initialize fields and mappings at the right time.
     */
    public void initTransformer () {
    
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
     * Reads nbt data from a persistent nbt object which is stored separately from the world.
     * 
     * @param nbt The nbt object to read from.
     */
    public void readNBT (NBTTagCompound nbt) {
    
    }
    
    /**
     * Writes nbt data to the persistent nbt object. This data is stored seperately from the
     * world.
     * 
     * @param nbt The nbt object to write to.
     */
    public void writeNBT (NBTTagCompound nbt) {
    
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
}