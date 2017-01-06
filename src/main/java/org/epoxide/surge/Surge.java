package org.epoxide.surge;

import org.epoxide.surge.common.ProxyCommon;
import org.epoxide.surge.features.Feature;
import org.epoxide.surge.features.FeatureManager;
import org.epoxide.surge.handler.PersistentDataHandler;
import org.epoxide.surge.libs.Constants;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION, dependencies = Constants.DEPENDENCIES, acceptableRemoteVersions = "*")
public class Surge {

    @SidedProxy(clientSide = Constants.CLIENT_PROXY_CLASS, serverSide = Constants.SERVER_PROXY_CLASS)
    public static ProxyCommon proxy;

    @Mod.Instance(Constants.MOD_ID)
    public static Surge instance;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        PersistentDataHandler.init();
        proxy.onPreInit();
        FeatureManager.FEATURES.forEach(Feature::onPreInit);

        for (final Feature feature : FeatureManager.FEATURES)
            if (feature.usesEvents())
                MinecraftForge.EVENT_BUS.register(feature);
    }

    @EventHandler
    public void init (FMLInitializationEvent event) {

        FeatureManager.FEATURES.forEach(Feature::onInit);
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent event) {

        FeatureManager.FEATURES.forEach(Feature::onPostInit);
    }

    @EventHandler
    public void onFMLFinished (FMLLoadCompleteEvent event) {

        FeatureManager.FEATURES.forEach(Feature::onFMLFinished);
    }
}