package net.epoxide.surge.features.rendering;

import net.epoxide.surge.asm.ASMUtils;
import net.epoxide.surge.features.Feature;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class FeatureDisableAnimation extends Feature {

    @Override
    public boolean enabledByDefault () {
        return false;
    }

    @Override
    public byte[] transform (String name, String transformedName, byte[] bytes) {
        if (this.enabled) {

            final ClassNode clazz = ASMUtils.createClassFromByteArray(bytes);
            this.transformLoadSpriteFrames(ASMUtils.getMethodFromClass(clazz, "loadSpriteFrames", "(Lnet/minecraft/client/resources/IResource;I)V"));
            return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        }

        return bytes;
    }

    private void transformLoadSpriteFrames (MethodNode method) {

        final InsnList needle = new InsnList();
        needle.add(new InsnNode(Opcodes.POP));
        needle.add(new LabelNode());
        needle.add(new LineNumberNode(-1, new LabelNode()));
        needle.add(new VarInsnNode(Opcodes.ALOAD, 4));

        final AbstractInsnNode pointer = ASMUtils.findLastNodeFromNeedle(method.instructions, needle);
        method.instructions.remove(pointer.getNext());
        final InsnList newInstr = new InsnList();

        LabelNode L6 = new LabelNode();
        newInstr.add(new JumpInsnNode(Opcodes.IFNULL, L6));
        newInstr.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/epoxide/surge/features/FeatureManager", "featureDisableAnimation", "Lnet/epoxide/surge/features/Feature;"));
        newInstr.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/epoxide/surge/features/Feature", "isEnabled", "()Z", false));

        needle.clear();
        needle.add(new VarInsnNode(Opcodes.ALOAD, 0));
        needle.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/texture/TextureAtlasSprite", "framesTextureData", "Ljava/util/List;"));
        needle.add(new VarInsnNode(Opcodes.ALOAD, 5));
        needle.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", false));
        needle.add(new InsnNode(Opcodes.POP));
        needle.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode()));
        needle.add(new LabelNode());

        AbstractInsnNode pointer2 = ASMUtils.findLastNodeFromNeedle(method.instructions, needle);
        newInstr.add(new JumpInsnNode(Opcodes.IFEQ, (LabelNode) pointer2));
        newInstr.add(L6);

        method.instructions.insert(pointer, newInstr);
    }
}
