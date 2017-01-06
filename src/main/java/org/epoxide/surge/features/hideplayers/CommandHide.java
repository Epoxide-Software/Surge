package org.epoxide.surge.features.hideplayers;

import org.epoxide.surge.command.SurgeCommand;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CommandHide implements SurgeCommand {

    @Override
    public String getSubName () {

        return "hideplayers";
    }

    @Override
    public void execute (ICommandSender sender, String[] args) {

        FeatureHidePlayer.toggleHiding();
        sender.addChatMessage(new TextComponentString(I18n.format("message.surge.hideplayers." + FeatureHidePlayer.isHiding())));
    }

    @Override
    public String getUsage () {

        return "hideplayers";
    }
}