package net.epoxide.surge;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class Asm implements IFMLLoadingPlugin {
    
    @Override
    public String[] getASMTransformerClass () {
        
        return new String[] { "net.epoxide.surge.Transformer" };
    }
    
    @Override
    public String getModContainerClass () {
        
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getSetupClass () {
        
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void injectData (Map<String, Object> data) {
        
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String getAccessTransformerClass () {
        
        // TODO Auto-generated method stub
        return null;
    }
    
}
