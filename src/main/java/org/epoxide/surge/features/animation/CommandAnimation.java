package org.epoxide.surge.features.animation;

import org.epoxide.surge.command.SurgeCommand;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CommandAnimation implements SurgeCommand {
    
    @Override
    public String getSubName () {
        
        return "animation";
    }
    
    @Override
    public String getUsage () {
        
        return "animation";
    }
    
    @Override
    public void execute (ICommandSender sender, String[] args) {
        
        FeatureDisableAnimation.toggleAnimation();
        sender.addChatMessage(new TextComponentString(I18n.format("message.surge.animation." + FeatureDisableAnimation.animationDisabled())));
    }
}
