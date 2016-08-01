package net.epoxide.surge.handler;

import java.io.File;

import net.epoxide.surge.features.Feature;
import net.epoxide.surge.features.FeatureManager;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {
    
    /**
     * An instance of the Configuration object being used.
     */
    private static Configuration config = null;
    
    /**
     * Initializes the configuration file.
     * 
     * @param file The file to read/write config stuff to.
     */
    public static void initConfig (File file) {
        
        config = new Configuration(file);
    }
    
    /**
     * Syncs all configuration properties.
     */
    public static void syncConfig () {
        
        config.setCategoryComment("_features", "Allows features to be completely disabled");
        
        for (final Feature feature : FeatureManager.FEATURES)
            feature.setupConfig(config);
            
        if (config.hasChanged())
            config.save();
    }
    
    /**
     * Checks if a feature is enabled.
     * 
     * @param feature The feature to check for.
     * @param name The name of the feature.
     * @param description The description for the feature.
     * @return Whether or not the feature was enabled.
     */
    public static boolean isFeatureEnabled (Feature feature, String name, String description) {
        
        return config.getBoolean(name, "_features", feature.enabledByDefault(), description);
    }
}