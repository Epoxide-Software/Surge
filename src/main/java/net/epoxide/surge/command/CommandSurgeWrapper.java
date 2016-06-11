package net.epoxide.surge.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSurgeWrapper extends CommandBase {
    
    private static Map<String, SurgeCommand> subCommands = new HashMap<>();
    
    public static void addCommand (SurgeCommand command) {
        
        subCommands.put(command.getSubName(), command);
    }
    
    @Override
    public String getCommandName () {
        
        return "surge";
    }
    
    @Override
    public String getCommandUsage (ICommandSender sender) {
        
        final StringBuilder builder = new StringBuilder("Commands:");
        subCommands.values().forEach(command -> builder.append(SystemUtils.LINE_SEPARATOR + command.getUsage()));
        
        return builder.toString();
    }
    
    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) {
        
        if (args.length > 0) {
            if (subCommands.containsKey(args[0])) {
                
                final SurgeCommand command = subCommands.get(args[0]);
                command.execute(server, sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        
        else
            sender.addChatMessage(new TextComponentString(this.getCommandUsage(sender)));
    }
}
