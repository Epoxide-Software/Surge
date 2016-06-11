package net.epoxide.surge.features;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface CommandFeatures {

    String getSubCommand();

    String getUsage();

    void execute(MinecraftServer server, ICommandSender sender, String[] args);
}
