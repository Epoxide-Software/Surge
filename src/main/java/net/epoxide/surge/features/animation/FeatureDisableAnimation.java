package net.epoxide.surge.features.animation;

import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.features.hideplayers.CommandHide;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.epoxide.surge.asm.ASMUtils;
import net.epoxide.surge.asm.Mappings;
import net.epoxide.surge.features.Feature;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Allows for animations to be disabled. This will usually improve performance, especially when
 * in areas with lots of animation like an ocean or the nether.
 */
@SideOnly(Side.CLIENT)
public class FeatureDisableAnimation extends Feature {

    public static boolean animationDisabled = false;

    @Override
    public void onInit() {

        CommandSurgeWrapper.addCommand(new CommandAnimation());
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        final ClassNode clazz = ASMUtils.createClassFromByteArray(bytes);
        this.transformUpdateAnimation(Mappings.METHOD_UPDATE_ANIMATION.getMethodNode(clazz));

        return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }

    private void transformUpdateAnimation(MethodNode method) {

        final InsnList newInstr = new InsnList();

        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/epoxide/surge/features/animation/FeatureDisableAnimation", "animationDisabled", "()Z", false));
        LabelNode L1 = new LabelNode();
        newInstr.add(new JumpInsnNode(Opcodes.IFEQ, L1));
        newInstr.add(new LabelNode());
        newInstr.add(new InsnNode(Opcodes.RETURN));
        newInstr.add(L1);

        method.instructions.insert(method.instructions.getFirst().getNext().getNext(), newInstr);
    }

    @Override
    public boolean isTransformer() {

        return true;
    }

    @Override
    public boolean shouldTransform(String name) {

        return name.equals("net.minecraft.client.renderer.texture.TextureAtlasSprite");
    }

    @Override
    public boolean enabledByDefault() {

        return false;
    }

    public static void toggleAnimation() {
        animationDisabled = !animationDisabled;
    }

    public static boolean animationDisabled() {
        return animationDisabled;
    }
}