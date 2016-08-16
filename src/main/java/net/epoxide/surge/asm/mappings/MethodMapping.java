package net.epoxide.surge.asm.mappings;

import net.epoxide.surge.asm.ASMUtils;
import net.epoxide.surge.libs.Constants;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodMapping extends Mapping {

    public MethodMapping(String srgName, String mcpName, Class<?> returnType, Class<?>... params) {

        this(ASMUtils.isSrg ? srgName : mcpName, returnType, params);
    }

    public MethodMapping(String name, Class<?> returnType, Class<?>... params) {

        super(name, MappingsUtil.getMethodDescriptor(returnType, params));
    }

    public MethodNode getMethodNode(ClassNode classNode) {

        for (final MethodNode mnode : classNode.methods)
            if (this.toString().equals(mnode.name) && this.descriptor.equals(mnode.desc))
                return mnode;

        Constants.LOG.warn(new RuntimeException(String.format("The method %s with descriptor %s could not be found in %s", this.toString(), this.descriptor, classNode.name)));
        return null;
    }
}
