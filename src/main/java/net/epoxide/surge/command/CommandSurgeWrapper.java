package net.epoxide.surge.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandSurgeWrapper extends CommandBase {
    
    private static Map<String, SurgeCommand> commands = new HashMap<>();
    
    public static void addCommand (SurgeCommand command) {
        
        commands.put(command.getSubName(), command);
    }
    
    @Override
    public String getCommandName () {
        
        return "surge";
    }
    
    @Override
    public String getCommandUsage (ICommandSender sender) {
        
        // TODO Change to localization
        final StringBuilder builder = new StringBuilder("Commands:");
        commands.values().forEach(command -> builder.append(SystemUtils.LINE_SEPARATOR + command.getUsage()));
        return builder.toString();
    }
    
    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        
        if (args.length > 0) {
            if (commands.containsKey(args[0])) {
                final SurgeCommand command = commands.get(args[0]);
                command.execute(server, sender, Arrays.copyOfRange(args, 1, args.length));
                
            }
        }
        else
            throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
    }
}
