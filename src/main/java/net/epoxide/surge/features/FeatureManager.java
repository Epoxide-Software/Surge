package net.epoxide.surge.features;

import java.util.ArrayList;
import java.util.List;

import net.epoxide.surge.features.bugfix.FeatureRedstoneFix;
import net.epoxide.surge.features.rendering.FeatureGroupRenderCulling;
import net.epoxide.surge.features.rendering.FeatureHidePlayer;
import net.epoxide.surge.features.rendering.FeatureHideUnseenEntities;

public class FeatureManager {
    
    /**
     * List of all registered features.
     */
    public static List<Feature> features = new ArrayList<>();
    
    /**
     * This method is called before any mods have had a chance to initialize. Constructors
     * should take care not to reference any actual game code.
     */
    public static void initFeatures () {
        
        FeatureManager.features.add(new FeatureHidePlayer());
        FeatureManager.features.add(new FeatureRedstoneFix());
        FeatureManager.features.add(new FeatureGroupRenderCulling());
        FeatureManager.features.add(new FeatureHideUnseenEntities());
    }
}
