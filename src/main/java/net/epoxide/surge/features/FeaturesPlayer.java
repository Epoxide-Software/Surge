package net.epoxide.surge.features;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.command.SurgeCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FeaturesPlayer extends Feature {
    
    public static boolean hidePlayer = true;
    public static List<UUID> whitelisted = new ArrayList<>();
    
    @Override
    public void onInit () {
        
        CommandSurgeWrapper.addCommand(new CommandWhiteList());
    }
    
    @Override
    public void onClientPreInit () {
        
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void hidePlayer (RenderPlayerEvent.Pre event) {
        
        if (hidePlayer && !whitelisted.contains(event.getEntityPlayer().getUniqueID()))
            event.setCanceled(true);
    }
    
    private class CommandWhiteList implements SurgeCommand {
        
        @Override
        public String getSubName () {
            
            return "whitelist";
        }
        
        @Override
        public String getUsage () {
            
            return "/surge whitelist [add|remove|list] [username]";
        }
        
        @Override
        public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            
            System.out.println("whitelist");
            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                // TODO implement
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("add")) {
                    final EntityPlayerMP entityPlayer = server.getPlayerList().getPlayerByUsername(args[1]);
                    if (entityPlayer != null) {
                        FeaturesPlayer.whitelisted.add(entityPlayer.getUniqueID());
                        System.out.println("done");
                    }
                    else {
                        // TODO Not found
                    }
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    final EntityPlayerMP entityPlayer = server.getPlayerList().getPlayerByUsername(args[1]);
                    if (entityPlayer != null)
                        whitelisted.remove(entityPlayer.getUniqueID());
                    else {
                        // TODO Not found
                    }
                }
            }
            else
                throw new WrongUsageException(this.getUsage(), new Object[0]);
        }
    }
}
