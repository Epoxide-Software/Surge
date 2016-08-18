package net.epoxide.surge.asm.mappings;

import net.epoxide.surge.asm.ASMUtils;

public class Mapping {

    /**
     * The name of the field or method represented by this mapping.
     */
    protected final String srgName;
    /**
     * The name of the field or method represented by this mapping.
     */
    protected final String mcpName;

    /**
     * The descriptor for the mapping. Only applicable to methods. Will be null for fields.
     */
    protected final String descriptor;

    /**
     * Creates a mapping which can be used in ASM byte code manipulation or reflection.
     *
     * @param srgName    The name of mapping in a srg environment.
     * @param mcpName    The name of the mapping in a mappend mcp environment.
     * @param descriptor The descriptor.
     */
    public Mapping (String srgName, String mcpName, String descriptor) {

        this.srgName = srgName;
        this.mcpName = mcpName;
        this.descriptor = descriptor;
    }

    /**
     * Creates a mapping which can be used in ASM byte code manipulation or reflection.
     *
     * @param name       The name of the mapping. This should be one you are 100% certain of.
     * @param descriptor The descriptor.
     */
    public Mapping (String name, String descriptor) {

        this.srgName = name;
        this.mcpName = srgName;
        this.descriptor = descriptor;
    }

    /**
     * Gets the descriptor of the mapping.
     *
     * @return The descriptor of the mapping.
     */
    public String getDescriptor () {

        return this.descriptor;
    }

    @Override
    public String toString () {

        return ASMUtils.isSrg ? srgName : mcpName;
    }
}
