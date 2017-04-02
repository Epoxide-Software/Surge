package org.epoxide.surge.features.fatalmodelload;

import org.epoxide.surge.features.Feature;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FeatureFatalModelLoading extends Feature {
    
    public static boolean areModsMissing () {
        
        return get("modsMissing") || get("wrongMC") || get("customError") || get("dupesFound") || get("modSorting") || get("j8onlymods");
    }
    
    public static boolean get (String fieldName) {
        
        return ReflectionHelper.getPrivateValue(FMLClientHandler.class, FMLClientHandler.instance(), fieldName) != null;
    }
}