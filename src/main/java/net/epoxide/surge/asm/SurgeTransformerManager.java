package net.epoxide.surge.asm;

import net.epoxide.surge.features.FeatureManager;
import net.minecraft.launchwrapper.IClassTransformer;

public class SurgeTransformerManager implements IClassTransformer {
    
    @Override
    public byte[] transform (String name, String transformedName, byte[] classBytes) {
        
        if (transformedName.equals("net.minecraftforge.fml.common.LoadController"))
            return FeatureManager.featureLoadTimes.transform(name, transformedName, classBytes);
        if (transformedName.equals("net.minecraft.client.renderer.RenderGlobal"))
            return FeatureManager.featureGPUClouds.transform(name, transformedName, classBytes);
        if (transformedName.equals("net.minecraft.client.renderer.texture.TextureAtlasSprite"))
            return FeatureManager.featureDisableAnimation.transform(name, transformedName, classBytes);
        return classBytes;
    }
}