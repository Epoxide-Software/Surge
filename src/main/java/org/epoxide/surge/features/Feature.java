package org.epoxide.surge.features;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModMetadata;

public class Feature {

    /**
     * Whether or not this feature is enabled.
     */
    public boolean enabled;

    /**
     * The category name used for this feature in the config file.
     */
    protected String configName;

    /**
     * Called when the mod enters the preInit phase of loading. This is after
     * {@link #setupConfiguration(Configuration)} but before {@link #onClientPreInit()}.
     */
    public void onPreInit () {

    }

    /**
     * Called when the mod enters the init phase of loading.
     */
    public void onInit () {

    }

    /**
     * Called when the mod enters the postInit phase of loading.
     */
    public void onPostInit () {

    }

    /**
     * Called when FML has finished loading mods.
     */
    public void onFMLFinished () {

    }

    /**
     * Checks if the feature listens to any events.
     *
     * @return Whether or not the feature listends to events.
     */
    public boolean usesEvents () {

        return false;
    }

    /**
     * Reads nbt data from a persistent nbt object which is stored separately from the world.
     *
     * @param nbt The nbt object to read from.
     */
    public void readNBT (NBTTagCompound nbt) {

    }

    /**
     * Writes nbt data to the persistent nbt object. This data is stored seperately from the
     * world.
     *
     * @param nbt The nbt object to write to.
     */
    public void writeNBT (NBTTagCompound nbt) {

    }

    /**
     * Called before {@link #onPreInit()}. Allows for configuration options to be
     * detected/generated. A feature being enabled or not is handled automatically by the
     * feature manager.
     *
     * @param config The configuration object to pull data from.
     */
    public void setupConfig (Configuration config) {

    }

    /**
     * Checks if the feature should be enabled by default.
     *
     * @return Whether or not the feature should be enabled by default.
     */
    public boolean enabledByDefault () {

        return true;
    }

    public boolean hasOptifine () {

        try {
            Class<?> optifineConfig = Class.forName("Config", false, Loader.instance().getModClassLoader());
            String optifineVersion = (String) optifineConfig.getField("VERSION").get(null);
            Map<String, Object> dummyOptifineMeta = ImmutableMap.<String, Object>builder().put("name", "Optifine").put("version", optifineVersion).build();
            ModMetadata optifineMetadata = MetadataCollection.from(getClass().getResourceAsStream("optifinemod.info"), "optifine").getMetadataForId("optifine", dummyOptifineMeta);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}