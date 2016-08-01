package net.epoxide.surge.features.analysis;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.epoxide.surge.asm.ASMUtils;
import net.epoxide.surge.features.Feature;
import net.epoxide.surge.libs.Constants;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLEvent;

public class FeatureLoadTimes extends Feature {
    
    public static void initializationTime (ModContainer mc, FMLEvent stateEvent, long startTime, long endTime) {
        
        Constants.LOGGER.info(String.format("%s - %s: %d ms", mc.getModId(), stateEvent.getClass().getSimpleName(), endTime - startTime));
    }
    
    @Override
    public boolean enabledByDefault () {
        
        return false;
    }
    
    @Override
    public byte[] transform (String name, String transformedName, byte[] bytes) {
        
        if (this.enabled) {
            
            final ClassNode clazz = ASMUtils.createClassFromByteArray(bytes);
            
            this.transformSendEventToModContainerInitial(ASMUtils.getMethodFromClass(clazz, "sendEventToModContainer", "(Lnet/minecraftforge/fml/common/event/FMLEvent;Lnet/minecraftforge/fml/common/ModContainer;)V"));
            this.transformSendEventToModContainerPost(ASMUtils.getMethodFromClass(clazz, "sendEventToModContainer", "(Lnet/minecraftforge/fml/common/event/FMLEvent;Lnet/minecraftforge/fml/common/ModContainer;)V"));
            
            return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        }
        
        return bytes;
    }
    
    private void transformSendEventToModContainerInitial (MethodNode method) {
        
        final InsnList needle = new InsnList();
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
        
        final AbstractInsnNode pointer = ASMUtils.findFirstNodeFromNeedle(method.instructions, needle);
        final InsnList newInstr = new InsnList();
        
        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false));
        newInstr.add(new VarInsnNode(Opcodes.LSTORE, 5));
        newInstr.add(new LabelNode());
        
        method.instructions.insert(pointer, newInstr);
    }
    
    private void transformSendEventToModContainerPost (MethodNode method) {
        
        final InsnList needle = new InsnList();
        needle.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/google/common/eventbus/EventBus", "post", "(Ljava/lang/Object;)V", false));
        
        final AbstractInsnNode pointer = ASMUtils.findFirstNodeFromNeedle(method.instructions, needle);
        final InsnList newInstr = new InsnList();
        
        newInstr.add(new LabelNode());
        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false));
        newInstr.add(new VarInsnNode(Opcodes.LSTORE, 7));
        newInstr.add(new LabelNode());
        newInstr.add(new VarInsnNode(Opcodes.ALOAD, 2));
        newInstr.add(new VarInsnNode(Opcodes.ALOAD, 1));
        newInstr.add(new VarInsnNode(Opcodes.LLOAD, 5));
        newInstr.add(new VarInsnNode(Opcodes.LLOAD, 7));
        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/epoxide/surge/features/analysis/FeatureLoadTimes", "initializationTime", "(Lnet/minecraftforge/fml/common/ModContainer;Lnet/minecraftforge/fml/common/event/FMLEvent;JJ)V", false));
        newInstr.add(new LabelNode());
        
        method.instructions.insert(pointer, newInstr);
    }
}
