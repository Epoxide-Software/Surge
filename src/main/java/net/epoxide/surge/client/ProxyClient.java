package net.epoxide.surge.client;

import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.common.ProxyCommon;
import net.epoxide.surge.features.FeatureManager;
import net.epoxide.surge.features.animation.FeatureDisableAnimation;
import net.epoxide.surge.features.hideplayers.FeatureHidePlayer;
import net.epoxide.surge.features.pigsleep.FeaturePigmanSleep;
import net.minecraftforge.client.ClientCommandHandler;

public class ProxyClient extends ProxyCommon {

    @Override
    public void onPreInit () {

        ClientCommandHandler.instance.registerCommand(new CommandSurgeWrapper());
    }
    
    @Override
    public void registerFeatures() {
        
        FeatureManager.registerFeature(new FeatureHidePlayer(), "Hide Players", "Command to disable the rendering of other players on the client.");
        FeatureManager.registerFeature(new FeatureDisableAnimation(), "Disable Animation", "Allows the animation of block/item textures to be disabled.");
        FeatureManager.registerFeature(new FeaturePigmanSleep(), "Pigman Sleep", "Allow the player to sleep while pigman are around, unless angered");
    }
}