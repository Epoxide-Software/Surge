package net.epoxide.surge.features.gpucloud;

import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.features.animation.CommandAnimation;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

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

    private static boolean renderClouds = false;
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
    public void onInit() {

        CommandSurgeWrapper.addCommand(new CommandClouds());
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
        needle.add(new VarInsnNode(Opcodes.ALOAD, 0));
        needle.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/RenderGlobal", "mc", "Lnet/minecraft/client/Minecraft;"));
        needle.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/IRenderHandler", "render", "(FLnet/minecraft/client/multiplayer/WorldClient;Lnet/minecraft/client/Minecraft;)V", false));
        needle.add(new LabelNode());
        needle.add(new LineNumberNode(-1, new LabelNode()));
        needle.add(new InsnNode(Opcodes.RETURN));
        needle.add(new LabelNode());
        needle.add(new LineNumberNode(-1, new LabelNode()));

        final AbstractInsnNode pointer = ASMUtils.findFirstNodeFromNeedle(method.instructions, needle).getNext();
        final InsnList newInstr = new InsnList();
        
        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/epoxide/surge/features/gpucloud/FeatureGPUClouds", "getInstance", "()Lnet/epoxide/surge/features/gpucloud/CloudRenderer;", false));
        newInstr.add(new VarInsnNode(Opcodes.FLOAD, 1));
        newInstr.add(new VarInsnNode(Opcodes.ALOAD, 0));
        newInstr.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/RenderGlobal", "cloudTickCounter", "I"));
        newInstr.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/epoxide/surge/features/gpucloud/CloudRenderer", "render", "(FI)Z", false));
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

    public static boolean shouldRenderClouds() {
        return renderClouds;
    }

    public static void toggleRenderClouds() {
        renderClouds = !renderClouds;
    }
}