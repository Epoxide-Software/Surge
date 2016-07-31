package net.epoxide.surge.asm.features;

import net.epoxide.surge.asm.ASMUtils;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLEvent;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ASMFeatureDebugInit {

    public static void initializationTime (ModContainer mc, FMLEvent stateEvent, long startTime, long endTime) {
        long elapsed = endTime - startTime;

        System.out.println(mc.getModId() + " - " + stateEvent.getClass().getSimpleName() + ": " + elapsed + " ms");
    }

    public static void renderEntityTime (Entity entityIn, long startTime, long endTime) {
        long elapsed = endTime - startTime;

        System.out.println(entityIn.getClass() + ": " + elapsed + " ms");
    }

    public static byte[] transform (String name, String transformedName, byte[] bytes) {

        ClassNode clazz = ASMUtils.createClassFromByteArray(bytes);

        transformSendEventToModContainerInitial(ASMUtils.getMethodFromClass(clazz, "sendEventToModContainer", "(Lnet/minecraftforge/fml/common/event/FMLEvent;Lnet/minecraftforge/fml/common/ModContainer;)V"));
        transformSendEventToModContainerPost(ASMUtils.getMethodFromClass(clazz, "sendEventToModContainer", "(Lnet/minecraftforge/fml/common/event/FMLEvent;Lnet/minecraftforge/fml/common/ModContainer;)V"));

        return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }

    private static void transformSendEventToModContainerInitial (MethodNode method) {
        InsnList needle = new InsnList();
        needle.add(new LdcInsnNode("Sending event %s to mod %s"));
        needle.add(new InsnNode(Opcodes.ICONST_2));
        needle.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
        needle.add(new InsnNode(Opcodes.DUP));
        needle.add(new InsnNode(Opcodes.ICONST_0));
        needle.add(new VarInsnNode(Opcodes.ALOAD, 1));
        needle.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/event/FMLEvent", "getEventType", "()Ljava/lang/String;", false));
        needle.add(new InsnNode(Opcodes.AASTORE));
        needle.add(new InsnNode(Opcodes.DUP));
        needle.add(new InsnNode(Opcodes.ICONST_1));
        needle.add(new VarInsnNode(Opcodes.ALOAD, 3));
        needle.add(new InsnNode(Opcodes.AASTORE));
        needle.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/fml/common/FMLLog", "log", "(Ljava/lang/String;Lorg/apache/logging/log4j/Level;Ljava/lang/String;[Ljava/lang/Object;)V", false));
        needle.add(new LabelNode());
        needle.add(new LineNumberNode(-1, new LabelNode()));

        AbstractInsnNode pointer = ASMUtils.findFirstNodeFromNeedle(method.instructions, needle);

        InsnList newInstr = new InsnList();

        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false));
        newInstr.add(new VarInsnNode(Opcodes.LSTORE, 5));

        newInstr.add(new LabelNode());
        method.instructions.insert(pointer, newInstr);
    }

    private static void transformSendEventToModContainerPost (MethodNode method) {
        InsnList needle = new InsnList();
        needle.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/google/common/eventbus/EventBus", "post", "(Ljava/lang/Object;)V", false));

        AbstractInsnNode pointer = ASMUtils.findFirstNodeFromNeedle(method.instructions, needle);

        InsnList newInstr = new InsnList();

        newInstr.add(new LabelNode());
        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false));
        newInstr.add(new VarInsnNode(Opcodes.LSTORE, 7));

        newInstr.add(new LabelNode());
        newInstr.add(new VarInsnNode(Opcodes.ALOAD, 2));
        newInstr.add(new VarInsnNode(Opcodes.ALOAD, 1));
        newInstr.add(new VarInsnNode(Opcodes.LLOAD, 5));
        newInstr.add(new VarInsnNode(Opcodes.LLOAD, 7));
        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/epoxide/surge/asm/features/ASMFeatureDebugInit", "initializationTime", "(Lnet/minecraftforge/fml/common/ModContainer;Lnet/minecraftforge/fml/common/event/FMLEvent;JJ)V", false));

        newInstr.add(new LabelNode());

        method.instructions.insert(pointer, newInstr);
    }
}
