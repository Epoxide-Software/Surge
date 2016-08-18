package net.epoxide.surge.asm.mappings;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.epoxide.surge.libs.Constants;

public class MethodMapping extends Mapping {
    
    public MethodMapping(String name, Class<?> returnType, Class<?>... params) {
        
        this(name, name, returnType, params);
    }
    
    public MethodMapping(String srgName, String mcpName, Class<?> returnType, Class<?>... params) {
        
        super(srgName, mcpName, MappingsUtil.getMethodDescriptor(returnType, params));
    }
    
    public MethodNode getMethodNode (ClassNode classNode) {
        
        for (final MethodNode mnode : classNode.methods)
            if (this.toString().equals(mnode.name) && this.descriptor.equals(mnode.desc))
                return mnode;
                
        Constants.LOG.warn(new RuntimeException(String.format("The method %s with descriptor %s could not be found in %s", this.toString(), this.descriptor, classNode.name)));
        return null;
    }
}
