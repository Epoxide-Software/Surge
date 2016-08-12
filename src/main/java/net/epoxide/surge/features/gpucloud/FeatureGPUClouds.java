package net.epoxide.surge.features.gpucloud;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.epoxide.surge.asm.ASMUtils;
import net.epoxide.surge.asm.Mappings;
import net.epoxide.surge.features.Feature;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Replaces vanilla cloud rendering with one that uses the GPU for cloud geometry. This causes
 * significant improvements in cloud performance.
 */
@SideOnly(Side.CLIENT)
public class FeatureGPUClouds extends Feature {
    
    /**
     * Instance of the GPU cloud renderer.
     */
    private static CloudRenderer INSTANCE;
    
    /**
     * Gets the instance of the GPU cloud renderer.
     * 
     * @return The effectively final instance of the gpu cloud renderer.
     */
    public static CloudRenderer getInstance () {
        
        if (INSTANCE == null)
            INSTANCE = new CloudRenderer();
            
        return INSTANCE;
    }
    
    @Override
    public byte[] transform (String name, String transformedName, byte[] bytes) {
        
        final ClassNode clazz = ASMUtils.createClassFromByteArray(bytes);
        this.transformRenderClouds(Mappings.METHOD_RENDER_CLOUDS.getMethodNode(clazz));
        return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }
    
    private void transformRenderClouds (MethodNode method) {
        
        final InsnList needle = new InsnList();
        needle.add(new VarInsnNode(Opcodes.ALOAD, 0));
        needle.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/RenderGlobal", "mc", "Lnet/minecraft/client/Minecraft;"));
        needle.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "theWorld", "Lnet/minecraft/client/multiplayer/WorldClient;"));
        needle.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/WorldClient", "provider", "Lnet/minecraft/world/WorldProvider;"));
        needle.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/WorldProvider", "isSurfaceWorld", "()Z", false));
        needle.add(new JumpInsnNode(Opcodes.IFEQ, new LabelNode()));
        
        final AbstractInsnNode pointer = ASMUtils.findFirstNodeFromNeedle(method.instructions, needle).getNext();
        final InsnList newInstr = new InsnList();
        
        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/epoxide/surge/features/rendering/cloud/FeatureGPUClouds", "getInstance", "()Lnet/epoxide/surge/features/rendering/cloud/CloudRenderer;", false));
        newInstr.add(new VarInsnNode(Opcodes.FLOAD, 1));
        newInstr.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/epoxide/surge/features/rendering/cloud/CloudRenderer", "render", "(F)Z", false));
        final LabelNode label5 = new LabelNode();
        newInstr.add(new JumpInsnNode(Opcodes.IFEQ, label5));
        newInstr.add(new InsnNode(Opcodes.RETURN));
        newInstr.add(label5);
        
        method.instructions.insertBefore(pointer, newInstr);
    }
    
    @Override
    public boolean isTransformer () {
        
        return true;
    }
    
    @Override
    public boolean shouldTransform (String name) {
        
        return name.equals("net.minecraft.client.renderer.RenderGlobal");
    }
}