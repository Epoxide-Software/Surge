package net.epoxide.surge.features.renderculling;

import net.epoxide.surge.command.SurgeCommand;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

public class CommandGroupRenderCulling implements SurgeCommand {
    
    @Override
    public String getSubName () {
        
        return "renderCulling";
    }
    
    @Override
    public String getUsage () {
        
        return "renderCulling";
    }
    
    @Override
    public void execute (ICommandSender sender, String[] args) {
        
        FeatureGroupRenderCulling.toggleRenderCull();
        sender.addChatMessage(new TextComponentString(I18n.format("message.surge.renderCulling." + FeatureGroupRenderCulling.shouldRenderCull())));
    }
}
