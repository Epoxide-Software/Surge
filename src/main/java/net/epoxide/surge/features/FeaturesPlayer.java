package net.epoxide.surge.features;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.darkhax.bookshelf.lib.util.PlayerUtils;
import net.darkhax.bookshelf.lib.util.TextUtils;
import net.darkhax.bookshelf.lib.util.TextUtils.ChatFormat;
import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.command.SurgeCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Provides a way for the user to hide all other players. This can greatly reduce performance
 * issues on servers with a high concentration of players. This can also be beneficial to users
 * who want to record their game without other players getting in the way. This feature has two
 * parts. The first is the '/surge hideplayers' command which toggles this feature on/off. The
 * second feature is the '/surge whitelist [add|remove|list] command which allows the user to
 * manage exceptions to the hide players list. For example, if you want to only see your
 * friends, you can add them to the whitelist and they will not be hidden.
 */
public class FeaturesPlayer extends Feature {
    
    /**
     * A list containing the UUID of every whitelisted player.
     */
    private static final List<UUID> WHITELISTED = new ArrayList<>();
    
    /**
     * The UUID of the client player. Used to make sure the client player is always rendered.
     */
    private static UUID clientID = null;
    
    /**
     * The flag for whether or not this feature is enabled. Toggled using the /surge
     * hideplayers
     */
    private static boolean hidePlayers = false;
    
    @Override
    public void onInit () {
        
        clientID = PlayerUtils.fixStrippedUUID(Minecraft.getMinecraft().getSession().getPlayerID());
        CommandSurgeWrapper.addCommand(new CommandHidePlayers());
        CommandSurgeWrapper.addCommand(new CommandWhiteList());
    }
    
    @Override
    public void onClientPreInit () {
        
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onPlayerPreRender (RenderPlayerEvent.Pre event) {
        
        if (!event.getEntityPlayer().getUniqueID().equals(clientID) && hidePlayers && !WHITELISTED.contains(event.getEntityPlayer().getUniqueID()))
            event.setCanceled(true);
    }
    
    @SubscribeEvent
    public void onSpecialPreRender (RenderPlayerEvent.Specials.Pre event) {
        
        if (!event.getEntityPlayer().getUniqueID().equals(clientID) && hidePlayers && !WHITELISTED.contains(event.getEntityPlayer().getUniqueID()))
            event.setCanceled(true);
    }
    
    private class CommandHidePlayers implements SurgeCommand {
        
        @Override
        public String getSubName () {
            
            return "hideplayers";
        }
        
        @Override
        public void execute (ICommandSender sender, String[] args) {
            
            hidePlayers = !hidePlayers;
            sender.addChatMessage(new TextComponentString(I18n.format("message.surge.hideplayers." + hidePlayers)));
        }
        
        @Override
        public String getUsage () {
            
            return "hideplayers";
        }
    }
    
    private class CommandWhiteList implements SurgeCommand {
        
        @Override
        public String getSubName () {
            
            return "whitelist";
        }
        
        @Override
        public void execute (ICommandSender sender, String[] args) {
            
            if (args.length > 0) {
                
                final String commandName = args[0];
                
                if (commandName.equalsIgnoreCase("list")) {
                    
                    final StringBuilder builder = new StringBuilder(I18n.format("message.surge.whitelist.list"));
                    
                    for (final UUID uuid : WHITELISTED)
                        builder.append("\n> ").append(PlayerUtils.getPlayerNameFromUUID(uuid));
                        
                    sender.addChatMessage(new TextComponentString(builder.toString()));
                }
                
                if (args.length == 2) {
                    
                    final String username = args[1];
                    final UUID id = PlayerUtils.getUUIDFromName(username);
                    
                    if (id == null) {
                        
                        sender.addChatMessage(new TextComponentString(String.format("message.surge.whitelist.missing", TextUtils.formatString(username, ChatFormat.RED))));
                        return;
                    }
                    
                    if (commandName.equalsIgnoreCase("add")) {
                        
                        if (WHITELISTED.contains(id))
                            sender.addChatMessage(new TextComponentString(I18n.format("message.surge.whitelist.already", TextUtils.formatString(username, ChatFormat.RED))));
                            
                        else {
                            
                            WHITELISTED.add(id);
                            sender.addChatMessage(new TextComponentString(I18n.format("message.surge.whitelist", TextUtils.formatString(username, ChatFormat.GREEN))));
                        }
                    }
                    
                    else if (args[0].equalsIgnoreCase("remove"))
                        if (WHITELISTED.contains(id)) {
                            
                            WHITELISTED.remove(id);
                            sender.addChatMessage(new TextComponentString(I18n.format("message.surge.whitelist.removed", TextUtils.formatString(username, ChatFormat.RED))));
                        }
                        
                        else
                            sender.addChatMessage(new TextComponentString(I18n.format("message.surge.whitelist.not", TextUtils.formatString(username, ChatFormat.RED))));
                }
            }
            
            else
                sender.addChatMessage(new TextComponentString("/surge " + this.getUsage()));
        }
        
        @Override
        public String getUsage () {
            
            return "whitelist [add|remove|list] [username]";
        }
    }
}