package net.epoxide.surge.command;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;

public interface SurgeCommand {
    
    /**
     * Gets the name of the surge sub command being represented.
     * 
     * @return The name of the surge sub command.
     */
    String getSubName ();
    
    /**
     * Gets a usage string for the surge sub command.
     * 
     * @return The usage string for the surge sub command.
     */
    default String getUsage () {
        
        return I18n.format("command.surge." + getSubName() + ".usage");
    }
    
    /**
     * Handles execution of the surge sub command.
     * 
     * @param sender The player sending the command.
     * @param args The arguments for the command.
     */
    void execute (ICommandSender sender, String[] args);
}
