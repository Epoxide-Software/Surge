package net.epoxide.surge.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

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
    
    private final String path;
    
    private final boolean isStatic;
    
    /**
     * Creates a mapping which can be used in ASM byte code manipulation or reflection.
     * 
     * @param srgName The name of mapping in a srg environment.
     * @param mcpName The name of the mapping in a mappend mcp environment.
     * @param descriptor The descriptor.
     * @param path The path to the class which the mapping is in.
     */
    public Mapping(String srgName, String mcpName, String descriptor, String path, boolean isStatic) {
        
        this(ASMUtils.isSrg ? srgName : mcpName, descriptor, path, isStatic);
    }
    
    /**
     * Creates a mapping which can be used in ASM byte code manipulation or reflection.
     * 
     * @param name The name of the mapping. This should be one you are 100% certain of.
     * @param descriptor The descriptor.
     * @param path The path to the class which the mapping is in.
     */
    public Mapping(String name, String descriptor, String path, boolean isStatic) {
        
        this.name = name;
        this.descriptor = descriptor;
        this.path = path;
        this.isStatic = isStatic;
    }
    
    /**
     * Gets the descriptor of the mapping.
     * 
     * @return The descriptor of the mapping.
     */
    public String getDescriptor () {
        
        return this.descriptor;
    }
    
    public String getPath () {
        
        return this.path;
    }
    
    public FieldInsnNode getFieldNode () {
        
        return new FieldInsnNode(this.isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD, this.path, this.name, this.descriptor);
    }
    
    public MethodNode getMethodNode (ClassNode classNode) {
        
        for (final MethodNode mnode : classNode.methods)
            if (this.name.equals(mnode.name) && this.descriptor.equals(mnode.desc))
                return mnode;
                
        Constants.LOG.warn(new RuntimeException(String.format("The method %s with descriptor %s could not be found in %s", this.name, this.descriptor, classNode.name)));
        return null;
    }
    
    @Override
    public String toString () {
        
        return this.name;
    }
}
