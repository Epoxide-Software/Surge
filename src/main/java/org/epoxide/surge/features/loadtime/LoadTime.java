package org.epoxide.surge.features.loadtime;

public class LoadTime {
    
    /**
     * The ID of the mod being loaded.
     */
    private final String modID;
    
    /**
     * The amount of time in miliseconds.
     */
    private final long time;
    
    /**
     * Constructs a new load time object.
     *
     * @param modID The ID of the mod being loaded.
     * @param time The amount of time it took to load.
     */
    public LoadTime (String modID, long time) {
        
        this.modID = modID;
        this.time = time;
    }
    
    /**
     * Gets the load time in miliseconds.
     *
     * @return The load time in miliseconds.
     */
    public long getTime () {
        
        return this.time;
    }
    
    @Override
    public String toString () {
        
        return String.format("%s - %.2f seconds", this.modID, this.time / 1000d);
    }
}