package org.epoxide.surge.client;

import org.epoxide.surge.command.CommandSurgeWrapper;
import org.epoxide.surge.common.ProxyCommon;
import org.epoxide.surge.features.FeatureManager;
import org.epoxide.surge.features.animation.FeatureDisableAnimation;
import org.epoxide.surge.features.gpucloud.FeatureGPUClouds;
import org.epoxide.surge.features.hideplayers.FeatureHidePlayer;
import org.epoxide.surge.features.pigsleep.FeaturePigmanSleep;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ProxyClient extends ProxyCommon {

    @Override
    public void onPreInit () {

        ClientCommandHandler.instance.registerCommand(new CommandSurgeWrapper());
    }

    @SideOnly(Side.CLIENT)
    public static void registerClient () {

        FeatureManager.registerFeature(new FeatureGPUClouds(), "Cloud Rendering", "Switches the RenderGlobal to render clouds using GPU to render.");
        FeatureManager.registerFeature(new FeatureHidePlayer(), "Hide Players", "Command to disable the rendering of other players on the client.");
        FeatureManager.registerFeature(new FeatureDisableAnimation(), "Disable Animation", "Allows the animation of block/item textures to be disabled.");
        FeatureManager.registerFeature(new FeaturePigmanSleep(), "Pigman Sleep", "Allow the player to sleep while pigman are around, unless angered");
    }
}