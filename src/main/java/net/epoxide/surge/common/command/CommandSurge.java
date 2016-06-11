package net.epoxide.surge.common.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.epoxide.surge.features.CommandFeatures;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandSurge extends CommandBase {
    
    private static Map<String, CommandFeatures> commands = new HashMap<>();
    
    public static void addCommand (CommandFeatures command) {
        
        commands.put(command.getSubCommand(), command);
    }
    
    @Override
    public String getCommandName () {
        
        return "surge";
    }
    
    @Override
    public String getCommandUsage (ICommandSender sender) {
        
        // TODO Change to localization
        final StringBuilder builder = new StringBuilder("Commands:");
        commands.values().forEach(command -> builder.append("\n" + command.getUsage()));
        return builder.toString();
    }
    
    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        
        if (args.length > 0) {
            if (commands.containsKey(args[0])) {
                final CommandFeatures command = commands.get(args[0]);
                command.execute(server, sender, Arrays.copyOfRange(args, 1, args.length));
                
            }
        }
        else
            throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
    }
}
