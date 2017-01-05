package net.epoxide.surge.features.loadtime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.epoxide.surge.features.Feature;
import net.epoxide.surge.libs.Constants;
import net.epoxide.surge.libs.TextUtils;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLEvent;
import org.apache.commons.lang3.SystemUtils;

/**
 * Tracks the load time for mods, at various load stages. While these load times are not 100%
 * accurate, they do provide a glimpse into what is going on.
 */
public class FeatureLoadTimes extends Feature {

    public String CLASS_LOAD_CONTROLLER;
    //    public Mapping METHOD_SEND_EVENT_TO_MOD_CONTAINER;

    /**
     * A map that holds the load time of all mods, at various stages.
     */
    private static final HashMap<String, List<LoadTime>> LOAD_TIMES = new HashMap<>();

    /**
     * A map which holds the combined load time of every stage.
     */
    private static final HashMap<String, Long> LOAD_TOTAL_TIME = new HashMap<>();

    /**
     * Format to use when representing the current data and time.
     */
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    @Override
    public void onFMLFinished () {

        final File surgeDirectory = new File("surge");
        final String timestamp = TIME_FORMAT.format(new Date());

        if (!surgeDirectory.exists())
            surgeDirectory.mkdirs();

        final File loadDirectory = new File(surgeDirectory, "loadtimes");

        if (!loadDirectory.exists())
            loadDirectory.mkdirs();

        try (FileWriter writer = new FileWriter(new File(loadDirectory, "Surge-Load-Time-Analysis-" + timestamp + ".txt"))) {

            writer.write("#Surge Load Time Analysis - " + timestamp + SystemUtils.LINE_SEPARATOR);

            for (final String line : TextUtils.wrapStringToList("This file contains approximate information about how long each mod takes to load. The load time of each mod is split into groups which represent the loading stages of the game. If a mod does not have a load time listed, it took less than 0.01 seconds to load. Please note that a mod being on this list does not mean it is slow or broken. While this can be the case, load times can vary depending on how much content a mod provides.", 80, false, new ArrayList<String>()))
                writer.write(line + SystemUtils.LINE_SEPARATOR);

            writer.write(SystemUtils.LINE_SEPARATOR);

            long totalTime = 0;

            for (final String key : LOAD_TOTAL_TIME.keySet())
                totalTime += LOAD_TOTAL_TIME.get(key);

            writer.write(String.format("Total time: %.2f sec", totalTime / 1000d) + SystemUtils.LINE_SEPARATOR);

            writer.write(SystemUtils.LINE_SEPARATOR);

            for (final String key : LOAD_TIMES.keySet()) {

                writer.write(String.format("#%s - %.2f sec", key, LOAD_TOTAL_TIME.get(key) / 1000d) + SystemUtils.LINE_SEPARATOR);

                final List<LoadTime> times = LOAD_TIMES.get(key);
                times.sort((a, b) -> a.getTime() < b.getTime() ? 1 : a.getTime() == b.getTime() ? 0 : -1);

                for (final LoadTime time : times)
                    writer.write(time.toString() + SystemUtils.LINE_SEPARATOR);

                writer.write(SystemUtils.LINE_SEPARATOR);
            }
        }
        catch (final IOException exception) {

            Constants.LOG.warn(exception);
        }
    }

    /**
     * Stores the loading time of a mod.
     *
     * @param mod The mod being loaded.
     * @param stateEvent The event being tracked.
     * @param startTime The time the event started.
     * @param endTime The time the event ended.
     */
    public static void registerLoadingTime (ModContainer mod, FMLEvent stateEvent, long startTime, long endTime) {

        final String stageName = stateEvent.getClass().getSimpleName();
        final long elapsed = endTime - startTime;

        if (elapsed < 10)
            return;

        final LoadTime loadTime = new LoadTime(mod.getName(), elapsed);

        final Long totalTime = LOAD_TOTAL_TIME.get(stageName);

        if (totalTime == null)
            LOAD_TOTAL_TIME.put(stageName, elapsed);

        else
            LOAD_TOTAL_TIME.put(stageName, totalTime + elapsed);

        if (LOAD_TIMES.containsKey(stageName))
            LOAD_TIMES.get(stageName).add(loadTime);

        else {

            final List<LoadTime> times = new ArrayList<>();
            times.add(loadTime);
            LOAD_TIMES.put(stageName, times);
        }
    }

    @Override
    public boolean enabledByDefault () {

        return false;
    }
}
