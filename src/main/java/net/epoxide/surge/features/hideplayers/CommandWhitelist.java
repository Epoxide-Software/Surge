package net.epoxide.surge.features.hideplayers;

import java.util.List;
import java.util.UUID;

import net.epoxide.surge.command.SurgeCommand;
import net.epoxide.surge.libs.PlayerUtils;
import net.epoxide.surge.libs.TextUtils;
import net.epoxide.surge.libs.TextUtils.ChatFormat;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CommandWhitelist implements SurgeCommand {

    @Override
    public String getSubName () {

        return "whitelist";
    }

    @Override
    public void execute (ICommandSender sender, String[] args) {

        if (args.length > 0) {

            final List<UUID> whitelist = FeatureHidePlayer.getWhitelist();
            final String commandName = args[0];

            if (commandName.equalsIgnoreCase("list")) {

                final StringBuilder builder = new StringBuilder(I18n.format("message.surge.whitelist.list"));

                for (final UUID uuid : whitelist)
                    builder.append("\n> ").append(PlayerUtils.getPlayerNameFromUUID(uuid));

                sender.addChatMessage(new TextComponentString(builder.toString()));
            }

            if (args.length == 2) {

                final String username = args[1];
                final UUID id = PlayerUtils.getUUIDFromName(username);

                if (id == null) {

                    sender.addChatMessage(new TextComponentString(I18n.format("message.surge.whitelist.missing", TextUtils.formatString(username, ChatFormat.RED))));
                    return;
                }

                if (commandName.equalsIgnoreCase("add")) {

                    if (whitelist.contains(id))
                        sender.addChatMessage(new TextComponentString(I18n.format("message.surge.whitelist.already", TextUtils.formatString(username, ChatFormat.RED))));

                    else {

                        whitelist.add(id);
                        sender.addChatMessage(new TextComponentString(I18n.format("message.surge.whitelist", TextUtils.formatString(username, ChatFormat.GREEN))));
                    }
                }

                else if (args[0].equalsIgnoreCase("remove"))
                    if (whitelist.contains(id)) {

                        whitelist.remove(id);
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