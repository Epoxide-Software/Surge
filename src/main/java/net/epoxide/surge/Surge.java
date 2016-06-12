package net.epoxide.surge;

import java.util.ArrayList;
import java.util.List;

import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.common.ProxyCommon;
import net.epoxide.surge.features.Feature;
import net.epoxide.surge.features.FeaturesPlayer;
import net.epoxide.surge.handler.ConfigurationHandler;
import net.epoxide.surge.libs.Constants;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION_NUMBER, dependencies = Constants.DEPENDENCIES, acceptableRemoteVersions = "*")
public class Surge {
    
    public static List<Feature> features = new ArrayList<>();
    
    @SidedProxy(clientSide = Constants.CLIENT_PROXY_CLASS, serverSide = Constants.SERVER_PROXY_CLASS)
    public static ProxyCommon proxy;
    
    @Mod.Instance(Constants.MOD_ID)
    public static Surge instance;
    
    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        
        features.add(new FeaturesPlayer());
        
        ConfigurationHandler.initConfig(event.getSuggestedConfigurationFile());

        proxy.onPreInit();
        features.forEach(Feature::onPreInit);
    }
    
    @EventHandler
    public void init (FMLInitializationEvent event) {
        
        proxy.onInit();
        features.forEach(Feature::onInit);
    }
    
    @EventHandler
    public void postInit (FMLPostInitializationEvent event) {
        
        proxy.onPostInit();
        features.forEach(Feature::onPostInit);
    }
}