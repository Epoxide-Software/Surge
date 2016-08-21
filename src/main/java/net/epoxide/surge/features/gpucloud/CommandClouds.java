package net.epoxide.surge.features.gpucloud;

import net.epoxide.surge.command.SurgeCommand;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CommandClouds implements SurgeCommand {
    
    @Override
    public String getSubName () {
        
        return "clouds";
    }
    
    @Override
    public String getUsage () {
        
        return "clouds";
    }
    
    @Override
    public void execute (ICommandSender sender, String[] args) {
        
        FeatureGPUClouds.toggleRenderClouds();
        sender.addChatMessage(new TextComponentString(I18n.format("message.surge.clouds." + FeatureGPUClouds.shouldRenderClouds())));
    }
}
