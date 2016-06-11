package net.epoxide.surge.client;

import net.epoxide.surge.Surge;
import net.epoxide.surge.common.ProxyCommon;
import net.epoxide.surge.features.Features;
import net.minecraftforge.common.MinecraftForge;

public class ProxyClient extends ProxyCommon {

    @Override
    public void onPreInit() {

        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

        Surge.features.forEach(Features::setupRendering);
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onPostInit() {

    }
}