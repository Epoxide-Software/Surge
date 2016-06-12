package net.epoxide.surge.features;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.command.SurgeCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FeaturesPlayer extends Feature {

    public static boolean hidePlayer = true;
    public static List<UUID> whitelisted = new ArrayList<>();

    @Override
    public void onInit() {

        CommandSurgeWrapper.addCommand(new CommandWhiteList());
    }

    @Override
    public void onClientPreInit() {

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void hidePlayer(RenderPlayerEvent.Pre event) {

        if (hidePlayer && !whitelisted.contains(event.getEntityPlayer().getUniqueID()))
            event.setCanceled(true);
    }

    private class CommandWhiteList implements SurgeCommand {

        @Override
        public String getSubName() {

            return "whitelist";
        }

        @Override
        public String getUsage() {

            return "/surge whitelist [add|remove|list] [username]";
        }

        @Override
        public void execute(MinecraftServer nil, ICommandSender sender, String[] args) {

            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                StringBuilder builder = new StringBuilder("List of players whitelisted:");
                for (UUID uuid : whitelisted) {
                    final EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByUUID(uuid);
                    builder.append("\n> ").append(entityPlayer.getDisplayNameString());
                }
                sender.addChatMessage(new TextComponentString(builder.toString()));
            } else if (args.length == 2) {
                String username = args[1];
                if (args[0].equalsIgnoreCase("add")) {
                    final EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(username);
                    if (entityPlayer != null) {
                        if(whitelisted.contains(entityPlayer.getUniqueID())){
                            sender.addChatMessage(new TextComponentString(String.format("The EntityPlayer %s has already been whitelisted!", entityPlayer.getDisplayNameString())));
                        } else{
                            whitelisted.add(entityPlayer.getUniqueID());
                            sender.addChatMessage(new TextComponentString(String.format("The EntityPlayer %s has been added to your whitelist!", entityPlayer.getDisplayNameString())));
                        }

                    } else {
                        sender.addChatMessage(new TextComponentString(String.format("The EntityPlayer %s could not be found!", username)));
                    }
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    final EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(username);
                    if (entityPlayer != null) {
                        if(whitelisted.contains(entityPlayer.getUniqueID())){
                            whitelisted.remove(entityPlayer.getUniqueID());
                        } else{
                            sender.addChatMessage(new TextComponentString(String.format("The EntityPlayer %s is not whitelisted!", entityPlayer.getDisplayNameString())));
                        }
                    } else {
                        sender.addChatMessage(new TextComponentString(String.format("The EntityPlayer %s could not be found!", username)));
                    }
                }
            } else
                sender.addChatMessage(new TextComponentString(this.getUsage()));
        }
    }
}
