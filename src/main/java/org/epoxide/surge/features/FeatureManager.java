package org.epoxide.surge.features;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.epoxide.surge.client.ProxyClient;
import org.epoxide.surge.features.loadtime.FeatureLoadTimes;
import org.epoxide.surge.handler.ConfigurationHandler;

import net.minecraftforge.fml.common.FMLCommonHandler;

public class FeatureManager {

    /**
     * List of all registered features.
     */
    public static final List<Feature> FEATURES = new ArrayList<>();

    /**
     * This method is called before any mods have had a chance to initialize. Constructors
     * should take care not to reference any actual game code.
     */
    public static void initFeatures () {

        if (FMLCommonHandler.instance().getSide().isClient())
            ProxyClient.registerClient();

        registerFeature(new FeatureLoadTimes(), "Load Time Analysis", "Records the load time of all mods being loaded.");
    }

    /**
     * Registers a new feature with the feature manager. This will automatically create an
     * entry in the configuration file to enable/disable this feature. If the feature has been
     * disabled, it will not be registered. This will also handle event bus subscriptions.
     *
     * @param feature The feature being registered.
     * @param name The name of the feature.
     * @param description A short description of the feature.
     */
    public static void registerFeature (Feature feature, String name, String description) {

        feature.enabled = ConfigurationHandler.isFeatureEnabled(feature, name, description);

        if (feature.enabled) {

            feature.configName = name.toLowerCase().replace(' ', '_');
            FEATURES.add(feature);
        }
    }

    public static Feature getFeature (Class<? extends Feature> f) {

        if (FEATURES.size() == 0) {
            ConfigurationHandler.initConfig(new File("config/surge.cfg"));
            FeatureManager.initFeatures();
            ConfigurationHandler.syncConfig();
        }
        for (final Feature feature : FEATURES)
            if (feature.getClass() == f)
                return feature;
        return null;
    }
}
