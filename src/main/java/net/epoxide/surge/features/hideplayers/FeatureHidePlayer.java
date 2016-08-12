package net.epoxide.surge.features.hideplayers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.features.Feature;
import net.epoxide.surge.libs.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderPlayerEvent;
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
public class FeatureHidePlayer extends Feature {
    
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
        CommandSurgeWrapper.addCommand(new CommandHide());
        CommandSurgeWrapper.addCommand(new CommandWhitelist());
    }
    
    @SubscribeEvent
    public void onPlayerPreRender (RenderPlayerEvent.Pre event) {
        
        if (!event.getEntityPlayer().getUniqueID().equals(clientID) && hidePlayers && !WHITELISTED.contains(event.getEntityPlayer().getUniqueID()))
            event.setCanceled(true);
    }
    
    @SubscribeEvent
    public void onSpecialPreRender (@SuppressWarnings("deprecation") RenderPlayerEvent.Specials.Pre event) {
        
        if (!event.getEntityPlayer().getUniqueID().equals(clientID) && hidePlayers && !WHITELISTED.contains(event.getEntityPlayer().getUniqueID()))
            event.setCanceled(true);
    }
    
    /**
     * Gets access to the whitelist.
     * 
     * @return The player whitelist.
     */
    public static List<UUID> getWhitelist () {
        
        return WHITELISTED;
    }
    
    /**
     * Checks if players should be hidden.
     * 
     * @return Whether or not players are being hidden.
     */
    public static boolean isHiding () {
        
        return isHiding();
    }
    
    /**
     * Toggles whether or not players should be hidden.
     */
    public static void toggleHiding () {
        
        hidePlayers = !hidePlayers;
    }
    
    @Override
    public boolean usesEvents () {
        
        return true;
    }
}