package net.epoxide.surge.client;

import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.common.ProxyCommon;
import net.epoxide.surge.features.Feature;
import net.epoxide.surge.features.FeatureManager;
import net.minecraftforge.client.ClientCommandHandler;

public class ProxyClient extends ProxyCommon {
    
    @Override
    public void onPreInit () {
        
        FeatureManager.features.forEach(Feature::onClientPreInit);
        ClientCommandHandler.instance.registerCommand(new CommandSurgeWrapper());
    }
    
    @Override
    public void onInit () {
    
    }
    
    @Override
    public void onPostInit () {
    
    }
}