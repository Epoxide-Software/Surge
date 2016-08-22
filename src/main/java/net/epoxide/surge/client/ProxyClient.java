package net.epoxide.surge.client;

import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.common.ProxyCommon;
import net.minecraftforge.client.ClientCommandHandler;

public class ProxyClient extends ProxyCommon {
    
    @Override
    public void onPreInit () {
        
        ClientCommandHandler.instance.registerCommand(new CommandSurgeWrapper());
    }
}