package net.darkhax.surge;

import org.apache.logging.log4j.Logger;

import net.darkhax.surge.core.SurgeConfiguration;
import net.darkhax.surge.core.SurgeLoadingPlugin;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

@Mod(modid = "surge", name = "Surge", version = "@VERSION@", certificateFingerprint = "@FINGERPRINT@")
public class Surge {

    public static final Logger LOG = SurgeLoadingPlugin.LOG;

    @EventHandler
    public void onLoadComplete (FMLLoadCompleteEvent event) {

        if (SurgeConfiguration.showTotalLoadtime) {

            LOG.info("The game loaded in approximately  {} seconds.", (System.currentTimeMillis() - SurgeLoadingPlugin.firstLaunchTime) / 1000f);
        }
    }
}