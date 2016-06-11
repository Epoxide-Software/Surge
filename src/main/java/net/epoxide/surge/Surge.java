package net.epoxide.surge;

import net.epoxide.surge.common.ProxyCommon;
import net.epoxide.surge.handler.ForgeEventHandler;
import net.epoxide.surge.libs.Constants;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION_NUMBER, dependencies = Constants.DEPENDENCIES, acceptableRemoteVersions = "*")
public class Surge {

    @SidedProxy(clientSide = Constants.CLIENT_PROXY_CLASS, serverSide = Constants.SERVER_PROXY_CLASS)
    public static ProxyCommon proxy;

    @Mod.Instance(Constants.MOD_ID)
    public static Surge instance;

    public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("Surge");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

        proxy.onPreInit();

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        proxy.onInit();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        proxy.onPostInit();
    }
}