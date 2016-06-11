package net.epoxide.surge;

import net.epoxide.surge.common.ProxyCommon;
import net.epoxide.surge.common.command.CommandSurge;
import net.epoxide.surge.features.Features;
import net.epoxide.surge.features.FeaturesPlayer;
import net.epoxide.surge.libs.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION_NUMBER, dependencies = Constants.DEPENDENCIES, acceptableRemoteVersions = "*")
public class Surge {

    public static List<Features> features = new ArrayList<>();

    @SidedProxy(clientSide = Constants.CLIENT_PROXY_CLASS, serverSide = Constants.SERVER_PROXY_CLASS)
    public static ProxyCommon proxy;

    @Mod.Instance(Constants.MOD_ID)
    public static Surge instance;

    public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("Surge");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        features.add(new FeaturesPlayer());

        proxy.onPreInit();
        features.forEach(Features::onPreInit);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        proxy.onInit();
        features.forEach(Features::onInit);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        proxy.onPostInit();
        features.forEach(Features::onPostInit);
    }

    @EventHandler
    public void initCommand(FMLServerStartingEvent event) {

        event.registerServerCommand(new CommandSurge());
        features.forEach(feature-> feature.initCommands(event));
    }
}