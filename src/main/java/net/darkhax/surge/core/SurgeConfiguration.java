package net.darkhax.surge.core;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class SurgeConfiguration {

    public static Configuration config;

    // Load Times
    public static final String LOAD_TIME = "loadtime";

    public static boolean fastPrefixChecking;
    public static boolean checkForAnimatedModels;
    public static boolean disableAnimatedModels;

    // Performance
    public static final String PERFORMANCE = "performance";

    public static boolean sheepDyeBlendTable;

    // Bug Fix
    public static final String BUG_FIX = "bugfix";

    public static int maxRenameLength;

    // Misc Features
    public static final String MISC = "misc";

    public static boolean showTotalLoadtime;

    // Misc

    public static void init (File file) {

        if (config == null) {

            config = new Configuration(file);
            syncConfigData();
        }
    }

    public static void syncConfigData () {

        // Load Times
        fastPrefixChecking = config.getBoolean("fastPrefixChecking", LOAD_TIME, true, "Optimizes Forge's id prefix checking. Also removes prefix warnings which significantly impact load time in large quantities.");
        checkForAnimatedModels = config.getBoolean("checkForAnimatedModels", LOAD_TIME, true, "Improves model load times, by checking if an animated model exists before trying to load it.");
        disableAnimatedModels = config.getBoolean("disableAnimatedModels", LOAD_TIME, false, "Improves model load times by completely removing Forge's animated models. This is a faster version of checkForAnimatedModels");

        // Performance
        sheepDyeBlendTable = config.getBoolean("sheepDyeBlendTable", PERFORMANCE, true, "Replace sheep breeding to check a predefined table rather than querying the recipe registry.");

        // Buf Fix
        maxRenameLength = config.getInt("maxRenameLength", BUG_FIX, 256, 32, 1024, "The maximum number of characters that can be entered in an anvil.");

        // Misc
        showTotalLoadtime = config.getBoolean("showTotalLoadTime", MISC, true, "If true, the total load time will be printed in the console.");

        if (config.hasChanged()) {

            config.save();
        }
    }
}