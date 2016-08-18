package net.epoxide.surge.features.animation;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.epoxide.surge.asm.ASMUtils;
import net.epoxide.surge.asm.mappings.ClassMapping;
import net.epoxide.surge.asm.mappings.MethodMapping;
import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.features.Feature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Allows for animations to be disabled. This will usually improve performance, especially when
 * in areas with lots of animation like an ocean or the nether.
 */
@SideOnly(Side.CLIENT)
public class FeatureDisableAnimation extends Feature {
    
    private static final ClassMapping CLASS_TEXTURE_ATLAS_SPRITE = new ClassMapping("net.minecraft.client.renderer.texture.TextureAtlasSprite");
    private static final MethodMapping METHOD_UPDATE_ANIMATION = new MethodMapping("func_94219_l", "updateAnimation", void.class);
    
    /**
     * Whether or not animations should be displayed. Can be toggled via command.
     */
    private static boolean disableAnimations = true;
    
    @Override
    public void onInit () {
        
        CommandSurgeWrapper.addCommand(new CommandAnimation());
    }
    
    /**
     * Toggles the state of {@link #disableAnimations}. If it was false, it will become true.
     * The reverse is also true.
     */
    public static void toggleAnimation () {
        
        disableAnimations = !disableAnimations;
    }
    
    /**
     * Hook for checking if animations should be disabled. If this returns true, animated
     * textures will stay at their first frame.
     * 
     * WARNING: This method is a hook, which is referenced in ASM injections. Take care when
     * editing this method, as all references will need to be updated.
     * 
     * @return Whether or not animations should play.
     */
    public static boolean animationDisabled () {
        
        return disableAnimations;
    }

    /**
     * Transforms the update animation method to check our custom hook before updating. Allows animation to be disabled.
     * @param method TextureAtlasSprite#updateAnimation
     */
    private void transformUpdateAnimation (MethodNode method) {
        
        final InsnList newInstr = new InsnList();
        
        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/epoxide/surge/features/animation/FeatureDisableAnimation", "animationDisabled", "()Z", false));
        final LabelNode L1 = new LabelNode();
        newInstr.add(new JumpInsnNode(Opcodes.IFEQ, L1));
        newInstr.add(new LabelNode());
        newInstr.add(new InsnNode(Opcodes.RETURN));
        newInstr.add(L1);
        
        method.instructions.insert(method.instructions.getFirst().getNext().getNext(), newInstr);
    }
    
    @Override
    public byte[] transform (String name, String transformedName, byte[] bytes) {
        
        final ClassNode clazz = ASMUtils.createClassFromByteArray(bytes);
        this.transformUpdateAnimation(METHOD_UPDATE_ANIMATION.getMethodNode(clazz));
        return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }
    
    @Override
    public boolean isTransformer () {
        
        return true;
    }
    
    @Override
    public boolean shouldTransform (String name) {
        
        return CLASS_TEXTURE_ATLAS_SPRITE.isEqual(name);
    }
    
    @Override
    public boolean enabledByDefault () {
        
        return false;
    }
    
    @Override
    public void readNBT (NBTTagCompound nbt) {
        
        disableAnimations = nbt.getBoolean("animationDisabled");
    }
    
    @Override
    public void writeNBT (NBTTagCompound nbt) {
        
        nbt.setBoolean("animationDisabled", disableAnimations);
    }
}