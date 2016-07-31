package net.epoxide.surge.asm;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ASMConfig {
    
    private static Configuration config;
    
    public static void loadConfigOptions () {
        
        config = new Configuration(new File("config/surge_asm.cfg"));
        
        config.save();
    }
}