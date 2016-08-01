package net.epoxide.surge;

import net.epoxide.surge.common.ProxyCommon;
import net.epoxide.surge.features.Feature;
import net.epoxide.surge.features.FeatureManager;
import net.epoxide.surge.libs.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION_NUMBER, dependencies = Constants.DEPENDENCIES, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.9.4,1.10.2]")
public class Surge {
    
    @SidedProxy(clientSide = Constants.CLIENT_PROXY_CLASS, serverSide = Constants.SERVER_PROXY_CLASS)
    public static ProxyCommon proxy;
    
    @Mod.Instance(Constants.MOD_ID)
    public static Surge instance;
    
    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        
        proxy.onPreInit();
        FeatureManager.FEATURES.forEach(Feature::onPreInit);
    }
    
    @EventHandler
    public void init (FMLInitializationEvent event) {
        
        proxy.onInit();
        FeatureManager.FEATURES.forEach(Feature::onInit);
    }
    
    @EventHandler
    public void postInit (FMLPostInitializationEvent event) {
        
        proxy.onPostInit();
        FeatureManager.FEATURES.forEach(Feature::onPostInit);
    }
    
    @EventHandler
    public void onFMLFinished (FMLLoadCompleteEvent event) {
        
        FeatureManager.FEATURES.forEach(Feature::onFMLFinished);
    }
}