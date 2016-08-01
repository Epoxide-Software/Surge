package net.epoxide.surge.asm;

import net.epoxide.surge.features.analysis.FeatureLoadTimes;
import net.minecraft.launchwrapper.IClassTransformer;

public class SurgeTransformerManager implements IClassTransformer {
    
    @Override
    public byte[] transform (String name, String transformedName, byte[] classBytes) {
        
        if (transformedName.equals("net.minecraftforge.fml.common.LoadController"))
            return FeatureLoadTimes.transform(name, transformedName, classBytes);
        
        return classBytes;
    }
}