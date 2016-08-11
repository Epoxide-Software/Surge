package net.epoxide.surge.features.analysis;

public class LoadTime {
    
    private final String modID;
    private final long time;
    
    public LoadTime(String modID, long d) {
        
        this.modID = modID;
        this.time = d;
    }
    
    public long getTime () {
        
        return this.time;
    }
    
    @Override
    public String toString () {
        
        return String.format("%s - %.2f seconds", this.modID, this.time / 1000d);
    }
}