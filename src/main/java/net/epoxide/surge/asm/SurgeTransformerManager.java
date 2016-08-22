package net.epoxide.surge.asm;

import java.io.File;

import net.epoxide.surge.features.Feature;
import net.epoxide.surge.features.FeatureManager;
import net.epoxide.surge.handler.ConfigurationHandler;

import net.minecraft.launchwrapper.IClassTransformer;

public class SurgeTransformerManager implements IClassTransformer {

    boolean loading = false;
    boolean loaded = false;

    @Override
    public byte[] transform (String name, String transformedName, byte[] classBytes) {

        if (!loading) {
            loading = true;
            ConfigurationHandler.initConfig(new File("config/surge.cfg"));
            FeatureManager.initFeatures();
            ConfigurationHandler.syncConfig();
            for (final Feature feature : FeatureManager.TRANSFORMERS)
                feature.initTransformer();
            loaded = true;
        }
        if (loaded) {
            for (final Feature feature : FeatureManager.TRANSFORMERS)
                if (feature.shouldTransform(transformedName))
                    return feature.transform(name, transformedName, classBytes);
        }
        return classBytes;
    }
}