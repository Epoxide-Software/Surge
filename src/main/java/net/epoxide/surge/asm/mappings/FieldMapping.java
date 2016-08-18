package net.epoxide.surge.asm.mappings;

import org.objectweb.asm.tree.FieldInsnNode;

public class FieldMapping extends Mapping {
    private final ClassMapping classMapping;
    
    public FieldMapping(ClassMapping classMapping, String name, Class<?> clazz) {
        
        super(name, MappingsUtil.getDescriptorFromClass(clazz));
        this.classMapping = classMapping;
    }
    
    public FieldMapping(ClassMapping classMapping, String srgName, String mcpName, Class<?> clazz) {
        
        super(srgName, mcpName, MappingsUtil.getDescriptorFromClass(clazz));
        this.classMapping = classMapping;
    }
    
    public String getClassPath () {
        
        return this.classMapping.toString().replace(".", "/");
    }
    
    public FieldInsnNode getFieldNode (int opCode) {
        
        return new FieldInsnNode(opCode, this.getClassPath(), this.toString(), this.getDescriptor());
    }
}
