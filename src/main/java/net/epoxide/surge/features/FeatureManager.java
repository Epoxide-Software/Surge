package net.epoxide.surge.features;

import java.util.ArrayList;
import java.util.List;

import net.epoxide.surge.features.analysis.FeatureLoadTimes;
import net.epoxide.surge.features.bugfix.FeatureRedstoneFix;
import net.epoxide.surge.features.rendering.cloud.FeatureGPUClouds;
import net.epoxide.surge.features.rendering.FeatureGroupRenderCulling;
import net.epoxide.surge.features.rendering.FeatureHidePlayer;
import net.epoxide.surge.features.rendering.FeatureHideUnseenEntities;
import net.epoxide.surge.handler.ConfigurationHandler;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;

public class FeatureManager {
    
    /**
     * List of all registered features.
     */
    public static final List<Feature> FEATURES = new ArrayList<>();
    
    public static Feature featureHidePlayers;
    public static Feature featureRedstoneFix;
    public static Feature featureGroupRenderCulling;
    public static Feature featureHideUnseenEntities;
    public static Feature featureLoadTimes;
    public static Feature featureGPUClouds;

    /**
     * This method is called before any mods have had a chance to initialize. Constructors
     * should take care not to reference any actual game code.
     */
    public static void initFeatures () {
        
        if (FMLLaunchHandler.side() == Side.CLIENT) {
            
            featureHideUnseenEntities = registerFeature(new FeatureHideUnseenEntities(), "Hide Unseen Entities", "Prevents the rendering of entities that are not in view of the camera.");
            featureGroupRenderCulling = registerFeature(new FeatureGroupRenderCulling(), "Group Render Culling", "Cuts down on the amount of entities rendered, when they are bunched together.");
            featureHidePlayers = registerFeature(new FeatureHidePlayer(), "Hide Players", "Command to disable the rendering of other players on the client.");
            featureRedstoneFix = registerFeature(new FeatureRedstoneFix(), "Redstone Toggle Fix", "Fixes a memory leak with toggle state of redstone torches. MC-101233");
            featureGPUClouds = registerFeature(new FeatureGPUClouds(), "Cloud Rendering", "Switches the RenderGlobal to render clouds using GPU to render.");
        }
        
        featureLoadTimes = registerFeature(new FeatureLoadTimes(), "Load Time Analysis", "Records the load time of all mods being loaded.");
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
    private static Feature registerFeature (Feature feature, String name, String description) {
        
        feature.enabled = ConfigurationHandler.isFeatureEnabled(feature, name, description);
        
        if (feature.enabled) {
            
            feature.configName = name.toLowerCase().replace(' ', '_');
            FEATURES.add(feature);
        }
        
        return feature;
    }
}
