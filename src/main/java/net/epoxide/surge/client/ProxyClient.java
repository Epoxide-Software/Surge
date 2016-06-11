package net.epoxide.surge.client;

import net.epoxide.surge.Surge;
import net.epoxide.surge.common.ProxyCommon;
import net.epoxide.surge.features.Feature;

public class ProxyClient extends ProxyCommon {
    
    @Override
    public void onPreInit () {
        
        Surge.features.forEach(Feature::onClientPreInit);
    }
    
    @Override
    public void onInit () {
    
    }
    
    @Override
    public void onPostInit () {
    
    }
}