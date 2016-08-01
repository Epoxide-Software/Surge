package net.epoxide.surge.asm;

import java.io.File;
import java.util.Map;

import net.epoxide.surge.features.FeatureManager;
import net.epoxide.surge.handler.ConfigurationHandler;
import net.epoxide.surge.libs.Constants;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions("net.epoxide.surge.asm")
// Removing version as 1.9 and 1.10 are supported. @IFMLLoadingPlugin.MCVersion("1.10.2")
public class SurgeLoadingPlugin implements IFMLLoadingPlugin {
    
    @Override
    public String[] getASMTransformerClass () {
        
        ASMUtils.isASMEnabled = true;
        ConfigurationHandler.initConfig(new File("config/surge.cfg"));
        FeatureManager.initFeatures();
        ConfigurationHandler.syncConfig();
        Constants.LOGGER.info("Starting to apply transformations");
        return new String[] { SurgeTransformerManager.class.getName() };
    }
    
    @Override
    public String getModContainerClass () {
        
        return null;
    }
    
    @Override
    public String getSetupClass () {
        
        return null;
    }
    
    @Override
    public void injectData (Map<String, Object> data) {
        
        ASMUtils.isSrg = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }
    
    @Override
    public String getAccessTransformerClass () {
        
        return null;
    }
}