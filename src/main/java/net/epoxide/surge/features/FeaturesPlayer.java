package net.epoxide.surge.features;

import net.epoxide.surge.common.command.CommandSurge;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeaturesPlayer extends Features {

    public static boolean hidePlayer = true;
    public static List<UUID> whitelisted = new ArrayList<>();

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void setupRendering() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void hidePlayer(RenderPlayerEvent.Pre event) {
        if (hidePlayer && !whitelisted.contains(event.getEntityPlayer().getUniqueID()))
            event.setCanceled(true);
    }

    @Override
    public void initCommands(FMLServerStartingEvent event) {
        CommandSurge.addCommand(new CommandWhiteList());
    }

    private class CommandWhiteList implements CommandFeatures {

        @Override
        public String getSubCommand() {
            return "whitelist";
        }

        @Override
        public String getUsage() {
            return null;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) {

        }
    }
}
