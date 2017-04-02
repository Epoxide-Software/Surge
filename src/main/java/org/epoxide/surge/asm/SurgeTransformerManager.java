package org.epoxide.surge.asm;

import org.epoxide.surge.features.Feature;
import org.epoxide.surge.features.FeatureManager;
import org.epoxide.surge.features.animation.FeatureDisableAnimation;
import org.epoxide.surge.features.fatalmodelload.FeatureFatalModelLoading;
import org.epoxide.surge.features.loadtime.FeatureLoadTimes;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.client.FMLClientHandler;

public class SurgeTransformerManager implements IClassTransformer {
    
    @Override
    public byte[] transform (String name, String transformedName, byte[] classBytes) {
        
        if (transformedName.equals("net.minecraft.client.renderer.texture.TextureAtlasSprite") && !FMLClientHandler.instance().hasOptifine()) {
            final Feature f = FeatureManager.getFeature(FeatureDisableAnimation.class);
            if (FeatureManager.isEnabled(f)) {
                final ClassNode clazz = ASMUtils.createClassFromByteArray(classBytes);
                this.transformUpdateAnimation(ASMUtils.getMethodFromClass(clazz, ASMUtils.isSrg ? "func_94219_l" : "updateAnimation", "()V"));
                return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_MAXS);
            }
        }
        
        if (transformedName.equals("net.minecraftforge.fml.common.LoadController")) {
            final Feature f = FeatureManager.getFeature(FeatureLoadTimes.class);
            if (FeatureManager.isEnabled(f)) {
                final ClassNode clazz = ASMUtils.createClassFromByteArray(classBytes);
                this.transformSendEventToModContainer(ASMUtils.getMethodFromClass(clazz, "sendEventToModContainer", "(Lnet/minecraftforge/fml/common/event/FMLEvent;Lnet/minecraftforge/fml/common/ModContainer;)V"));
                return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_MAXS);
            }
        }
        
        if (transformedName.equals("net.minecraftforge.client.model.ModelLoader")) {
            
            final Feature f = FeatureManager.getFeature(FeatureFatalModelLoading.class);
            
            if (FeatureManager.isEnabled(f)) {
                
                final ClassNode clazz = ASMUtils.createClassFromByteArray(classBytes);
                this.transformSetupModelRegistry(ASMUtils.getMethodFromClass(clazz, "setupModelRegistry", "()Lnet/minecraft/util/registry/IRegistry;"));
                return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_MAXS);
            }
        }
        
        return classBytes;
    }
    
    /**
     * Label l0 = new Label(); mv.visitLabel(l0); mv.visitLineNumber(25, l0);
     * mv.visitMethodInsn(INVOKESTATIC,
     * "org/epoxide/surge/features/fatalmodelload/FeatureFatalModelLoading", "areModsMissing",
     * "()Z", false); Label l1 = new Label(); mv.visitJumpInsn(IFEQ, l1); Label l2 = new
     * Label(); mv.visitLabel(l2); mv.visitLineNumber(26, l2); mv.visitVarInsn(ALOAD, 0);
     * mv.visitFieldInsn(GETFIELD,
     * "org/epoxide/surge/features/fatalmodelload/FeatureFatalModelLoading", "bakedRegistry",
     * "Lnet/minecraft/util/registry/RegistrySimple;"); mv.visitInsn(ARETURN);
     *
     * @param method
     */
    private void transformSetupModelRegistry (MethodNode method) {
        
        final InsnList i = new InsnList();
        final LabelNode l0 = new LabelNode();
        i.add(l0);
        i.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/epoxide/surge/features/fatalmodelload/FeatureFatalModelLoading", "areModsMissing", "()Z", false));
        final LabelNode l1 = new LabelNode();
        i.add(new JumpInsnNode(Opcodes.IFEQ, l1));
        final LabelNode l2 = new LabelNode();
        i.add(l2);
        i.add(new VarInsnNode(Opcodes.ALOAD, 0));
        i.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/ModelLoader", "bakedRegistry", "Lnet/minecraft/util/registry/RegistrySimple;"));
        i.add(new InsnNode(Opcodes.ARETURN));
        i.add(l1);
        method.instructions.insertBefore(method.instructions.getFirst(), i);
    }
    
    /**
     * Transforms the update animation method to check our custom hook before updating. Allows
     * animation to be disabled.
     *
     * @param method TextureAtlasSprite#updateAnimation
     */
    private void transformUpdateAnimation (MethodNode method) {
        
        final InsnList newInstr = new InsnList();
        
        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/epoxide/surge/features/animation/FeatureDisableAnimation", "animationDisabled", "()Z", false));
        final LabelNode L1 = new LabelNode();
        newInstr.add(new JumpInsnNode(Opcodes.IFEQ, L1));
        newInstr.add(new LabelNode());
        newInstr.add(new InsnNode(Opcodes.RETURN));
        newInstr.add(L1);
        
        method.instructions.insert(method.instructions.getFirst().getNext().getNext(), newInstr);
    }
    
    private void transformSendEventToModContainer (MethodNode method) {
        
        {
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
            
            final AbstractInsnNode pointer = ASMUtils.findLastNodeFromNeedle(method.instructions, needle);
            final InsnList newInstr = new InsnList();
            
            newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false));
            newInstr.add(new VarInsnNode(Opcodes.LSTORE, 5));
            newInstr.add(new LabelNode());
            
            method.instructions.insert(pointer, newInstr);
        }
        {
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
            newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/epoxide/surge/features/loadtime/FeatureLoadTimes", "registerLoadingTime", "(Lnet/minecraftforge/fml/common/ModContainer;Lnet/minecraftforge/fml/common/event/FMLEvent;JJ)V", false));
            newInstr.add(new LabelNode());
            
            method.instructions.insert(pointer, newInstr);
        }
    }
}