package net.epoxide.surge.asm;

import net.epoxide.surge.asm.features.ASMFeatureDebugInit;

import net.minecraft.launchwrapper.IClassTransformer;

public class SurgeTransformerManager implements IClassTransformer {

    @Override
    public byte[] transform (String name, String transformedName, byte[] classBytes) {

        if(transformedName.equals("net.minecraftforge.fml.common.LoadController"))
            return ASMFeatureDebugInit.transform(name, transformedName, classBytes);
        return classBytes;
    }
}