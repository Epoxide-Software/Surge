package net.epoxide.surge.asm;

import net.epoxide.surge.features.Feature;
import net.epoxide.surge.features.FeatureManager;
import net.minecraft.launchwrapper.IClassTransformer;

public class SurgeTransformerManager implements IClassTransformer {
    
    @Override
    public byte[] transform (String name, String transformedName, byte[] classBytes) {
        
        for (final Feature feature : FeatureManager.TRANSFORMERS)
            if (feature.shouldTransform(transformedName))
                return feature.transform(name, transformedName, classBytes);
                
        return classBytes;
    }
}