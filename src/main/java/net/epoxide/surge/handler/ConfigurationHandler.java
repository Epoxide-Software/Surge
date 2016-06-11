package net.epoxide.surge.handler;

import java.io.File;

import net.epoxide.surge.Surge;
import net.epoxide.surge.features.Feature;
import net.epoxide.surge.libs.Constants;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigurationHandler {
    
    private static Configuration config;
    
    public ConfigurationHandler(File file) {
        
        config = new Configuration(file);
        MinecraftForge.EVENT_BUS.register(this);
        this.syncConfigData();
    }
    
    @SubscribeEvent
    public void onConfigChange (OnConfigChangedEvent event) {
        
        if (event.getModID().equals(Constants.MOD_ID))
            this.syncConfigData();
    }
    
    private void syncConfigData () {
        
        for (final Feature feature : Surge.features)
            feature.setupConfig(config);
            
        if (config.hasChanged())
            config.save();
    }
}