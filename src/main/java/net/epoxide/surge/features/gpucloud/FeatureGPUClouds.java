package net.epoxide.surge.features.gpucloud;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.epoxide.surge.asm.ASMUtils;
import net.epoxide.surge.asm.mappings.ClassMapping;
import net.epoxide.surge.asm.mappings.FieldMapping;
import net.epoxide.surge.asm.mappings.MethodMapping;
import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.features.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Replaces vanilla cloud rendering with one that uses the GPU for cloud geometry. This causes
 * significant improvements in cloud performance.
 */
@SideOnly(Side.CLIENT)
public class FeatureGPUClouds extends Feature {
    
    private final ClassMapping CLASS_MINECRAFT = new ClassMapping("net.minecraft.client.Minecraft");
    private final ClassMapping CLASS_RENDER_GLOBAL = new ClassMapping("net.minecraft.client.renderer.RenderGlobal");
    
    private final MethodMapping METHOD_RENDER_CLOUDS = new MethodMapping("func_180447_b", "renderClouds", void.class, float.class, int.class);
    
    private final FieldMapping FIELD_RENDERGLOBAL_MC = new FieldMapping(this.CLASS_RENDER_GLOBAL, "field_72777_q", "mc", Minecraft.class);
    private final FieldMapping FIELD_MINECRAFT_THEWORLD = new FieldMapping(this.CLASS_MINECRAFT, "field_71441_e", "theWorld", WorldClient.class);
    private final FieldMapping FIELD_RENDERGLOBAL_CLOUDTICKCOUNTER = new FieldMapping(this.CLASS_RENDER_GLOBAL, "field_72773_u", "cloudTickCounter", int.class);
    
    /**
     * Whether or not the new cloud renderer should be used. Can be toggled via command.
     */
    private static boolean renderClouds = true;
    
    /**
     * Instance of the GPU cloud renderer.
     */
    private static CloudRenderer INSTANCE;
    
    @Override
    public void onInit () {
        
        CommandSurgeWrapper.addCommand(new CommandClouds());
    }
    
    /**
     * Transforms the renderClouds method to take gpu cloud rendering into account.
     * 
     * @param method RenderGlobal#renderClouds
     */
    private void transformRenderClouds (MethodNode method) {
        
        final InsnList needle = new InsnList();
        needle.add(new VarInsnNode(Opcodes.ALOAD, 0));
        needle.add(this.FIELD_RENDERGLOBAL_MC.getFieldNode(Opcodes.GETFIELD));
        needle.add(this.FIELD_MINECRAFT_THEWORLD.getFieldNode(Opcodes.GETFIELD));
        needle.add(new VarInsnNode(Opcodes.ALOAD, 0));
        needle.add(this.FIELD_RENDERGLOBAL_MC.getFieldNode(Opcodes.GETFIELD));
        needle.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/IRenderHandler", "render", "(FLnet/minecraft/client/multiplayer/WorldClient;Lnet/minecraft/client/Minecraft;)V", false));
        needle.add(new LabelNode());
        needle.add(new LineNumberNode(-1, new LabelNode()));
        needle.add(new InsnNode(Opcodes.RETURN));
        needle.add(new LabelNode());
        needle.add(new LineNumberNode(-1, new LabelNode()));
        
        final AbstractInsnNode pointer = ASMUtils.findLastNodeFromNeedle(method.instructions, needle);
        final InsnList newInstr = new InsnList();
        
        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/epoxide/surge/features/gpucloud/FeatureGPUClouds", "getInstance", "()Lnet/epoxide/surge/features/gpucloud/CloudRenderer;", false));
        newInstr.add(new VarInsnNode(Opcodes.FLOAD, 1));
        newInstr.add(new VarInsnNode(Opcodes.ALOAD, 0));
        newInstr.add(this.FIELD_RENDERGLOBAL_CLOUDTICKCOUNTER.getFieldNode(Opcodes.GETFIELD));
        newInstr.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/epoxide/surge/features/gpucloud/CloudRenderer", "render", "(FI)Z", false));
        final LabelNode label5 = new LabelNode();
        newInstr.add(new JumpInsnNode(Opcodes.IFEQ, label5));
        newInstr.add(new InsnNode(Opcodes.RETURN));
        newInstr.add(label5);
        
        method.instructions.insert(pointer, newInstr);
    }
    
    /**
     * Toggles the state of {@link #renderClouds}. If it was true, it will become false. The
     * opposite is also true.
     */
    public static void toggleRenderClouds () {
        
        renderClouds = !renderClouds;
    }
    
    /**
     * A hook to allow cloud rendering to be replaced with the GPU geometry clouds.
     * 
     * WARNING: This method is referenced directly through ASM. Take care when editing it.
     * 
     * @return Whether or not the custom cloud renderer should be used.
     */
    public static boolean shouldRenderClouds () {
        
        return renderClouds;
    }
    
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
        this.transformRenderClouds(this.METHOD_RENDER_CLOUDS.getMethodNode(clazz));
        return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }
    
    @Override
    public boolean isTransformer () {
        
        return true;
    }
    
    @Override
    public boolean shouldTransform (String name) {
        
        return this.CLASS_RENDER_GLOBAL.isEqual(name);
    }
    
    @Override
    public void readNBT (NBTTagCompound nbt) {
        
        renderClouds = nbt.getBoolean("renderClouds");
    }
    
    @Override
    public void writeNBT (NBTTagCompound nbt) {
        
        nbt.setBoolean("renderClouds", renderClouds);
    }
}