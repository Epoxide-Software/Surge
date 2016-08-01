package net.epoxide.surge;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;

public class Transformer implements IClassTransformer {
    
    public static List<String> list = new ArrayList<String>();
    
    @Override
    public byte[] transform (String name, String transformedName, byte[] basicClass) {
        
        if (transformedName.contains("net.minecraftforge") && transformedName.contains(".event."))
            list.add(transformedName);
            
        return basicClass;
    }
    
}
