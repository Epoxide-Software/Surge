package net.epoxide.surge.handler;

import java.io.File;

import net.epoxide.surge.features.FeatureManager;
import net.epoxide.surge.libs.Constants;
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
        
        if (config != null) {
            
            Constants.LOGGER.warn("Configuration file has already been initialized. " + Thread.currentThread().getStackTrace().toString());
            return;
        }
        
        config = new Configuration(file);
        
        FeatureManager.features.forEach(feature -> feature.setupConfig(config));
        
        if (config.hasChanged())
            config.save();
    }
}