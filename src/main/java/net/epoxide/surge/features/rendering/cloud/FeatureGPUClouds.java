package net.epoxide.surge.features.rendering.cloud;

import net.epoxide.surge.asm.ASMUtils;
import net.epoxide.surge.features.Feature;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class FeatureGPUClouds extends Feature {
    private static CloudRenderer INSTANCE;

    public static CloudRenderer getInstance () {
        if (INSTANCE == null)
            INSTANCE = new CloudRenderer();

        return INSTANCE;
    }

    @Override
    public byte[] transform (String name, String transformedName, byte[] bytes) {
        if (this.enabled) {

            final ClassNode clazz = ASMUtils.createClassFromByteArray(bytes);
            this.transformRenderClouds(ASMUtils.getMethodFromClass(clazz, "renderClouds", "(FI)V"));
            return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        }

        return bytes;
    }

    private void transformRenderClouds (MethodNode method) {

        final InsnList needle = new InsnList();
        needle.add(new VarInsnNode(Opcodes.ALOAD, 0));
        needle.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/RenderGlobal", "mc", "Lnet/minecraft/client/Minecraft;"));
        needle.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "theWorld", "Lnet/minecraft/client/multiplayer/WorldClient;"));
        needle.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/WorldClient", "provider", "Lnet/minecraft/world/WorldProvider;"));
        needle.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/WorldProvider", "isSurfaceWorld", "()Z",false));
        needle.add(new JumpInsnNode(Opcodes.IFEQ, new LabelNode()));

        final AbstractInsnNode pointer = ASMUtils.findFirstNodeFromNeedle(method.instructions, needle).getNext();
        final InsnList newInstr = new InsnList();

        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/epoxide/surge/features/rendering/cloud/FeatureGPUClouds", "getInstance", "()Lnet/epoxide/surge/features/rendering/cloud/CloudRenderer;", false));
        newInstr.add(new VarInsnNode(Opcodes.FLOAD, 1));
        newInstr.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/epoxide/surge/features/rendering/cloud/CloudRenderer", "render", "(F)Z", false));
        LabelNode L5 = new LabelNode();
        newInstr.add(new JumpInsnNode(Opcodes.IFEQ, L5));
        newInstr.add(new InsnNode(Opcodes.RETURN));
        newInstr.add(L5);

        method.instructions.insertBefore(pointer, newInstr);
    }
}