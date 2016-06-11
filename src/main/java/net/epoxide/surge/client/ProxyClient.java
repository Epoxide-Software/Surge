package net.epoxide.surge.client;

import net.epoxide.surge.common.ProxyCommon;
import net.minecraftforge.common.MinecraftForge;

public class ProxyClient extends ProxyCommon {

    @Override
    public void onPreInit() {

        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onPostInit() {

    }
}