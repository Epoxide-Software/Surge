package org.epoxide.surge.handler;

import java.io.File;
import java.io.IOException;

import org.epoxide.surge.features.FeatureManager;
import org.epoxide.surge.libs.Constants;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PersistentDataHandler {

    /**
     * Reference to the physical save file where data is stored.
     */
    private static File saveFile;

    /**
     * Persistent nbt file that is saved to a file separate from the world.
     */
    public static NBTTagCompound persistentData;

    /**
     * Initializes the data handler. This should be called before saving anything!
     */
    public static void init () {

        MinecraftForge.EVENT_BUS.register(new PersistentDataHandler());
        readData();
    }

    /**
     * Reads nbt data from the save file and sets it to {@link #persistentData}.
     */
    public static void readData () {

        try {

            persistentData = CompressedStreamTools.read(getSaveFile());

            if (persistentData == null)
                persistentData = new NBTTagCompound();

            FeatureManager.FEATURES.forEach(feature -> feature.readNBT(persistentData));
        }

        catch (final IOException e) {

            Constants.LOG.warn("Issue loading data file! Please report! " + e);
        }
    }

    /**
     * Saves the data from {@link #persistentData} to the save file.
     */
    public static void saveData () {

        try {

            if (persistentData == null)
                persistentData = new NBTTagCompound();
            
            FeatureManager.FEATURES.forEach(feature -> feature.writeNBT(persistentData));
            CompressedStreamTools.write(persistentData, getSaveFile());
        }

        catch (final IOException e) {

            Constants.LOG.warn("Issue writing data file! Please report! " + e);
        }
    }

    /**
     * Gets the file to read/write data to.
     *
     * @return The file to read/write persistent data to.
     */
    public static File getSaveFile () {

        if (saveFile != null)
            return saveFile;

        final File surgeDirectory = new File("Surge");

        if (!surgeDirectory.exists())
            surgeDirectory.mkdirs();

        return new File(surgeDirectory, "SurgeData.nbt");
    }

    @SubscribeEvent
    public void onWorldSave (WorldEvent.Save event) {

        saveData();
    }
}
