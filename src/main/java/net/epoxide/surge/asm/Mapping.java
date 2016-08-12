package net.epoxide.surge.asm;

import net.epoxide.surge.libs.Constants;

public class Mapping {
    
    /**
     * The name of the field or method represented by this mapping.
     */
    private final String name;
    
    /**
     * The descriptor for the mapping. Only applicable to methods. Will be null for fields.
     */
    private final String descriptor;
    
    /**
     * Whether or not this mapping is for a method.
     */
    private final boolean isMethod;
    
    /**
     * Creates a mapping for a field.
     * 
     * @param srgName The srg name for the field. What MCP deobfuscates it to.
     * @param mcpName The mcp name for the field. What you see in your IDE.
     */
    public Mapping(String srgName, String mcpName) {
        
        this.name = ASMUtils.isSrg ? srgName : mcpName;
        this.descriptor = null;
        this.isMethod = false;
    }
    
    /**
     * Creates a mapping for a method.
     * 
     * @param srgName The srg name for the method. What MCP deobfuscates it to.
     * @param mcpName The mcp name for the method. What you see in your IDE.
     * @param descriptor The descriptor for the method.
     */
    public Mapping(String srgName, String mcpName, String descriptor) {
        
        this.name = ASMUtils.isSrg ? srgName : mcpName;
        this.descriptor = descriptor;
        this.isMethod = true;
    }
    
    /**
     * Gets the descriptor for the mapping. This describes the parameters for a method, and the
     * return type. This will return null if you try to use it on a field mapping, and will
     * also throw a runtime exception!
     * 
     * @return The descriptor for the mapping.
     */
    public String getDescriptor () {
        
        if (!this.isMethod)
            Constants.LOG.warn(new RuntimeException("Attempted to get descriptor for a field! " + this.name));
        return this.descriptor;
    }
    
    /**
     * Checks if the mapping defines a method or a field.
     * 
     * @return Whether or not the mapping defines a field.
     */
    public boolean isMethod () {
        
        return this.isMethod;
    }
    
    @Override
    public String toString () {
        
        return this.name;
    }
}
