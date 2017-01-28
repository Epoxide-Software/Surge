package org.epoxide.surge.asm;

import org.epoxide.surge.features.Feature;
import org.epoxide.surge.features.FeatureManager;
import org.epoxide.surge.features.animation.FeatureDisableAnimation;
import org.epoxide.surge.features.bedbug.FeatureBedBug;
import org.epoxide.surge.features.loadtime.FeatureLoadTimes;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class SurgeTransformerManager implements IClassTransformer {

    @Override
    public byte[] transform (String name, String transformedName, byte[] classBytes) {

        if (transformedName.equals("net.minecraft.client.renderer.texture.TextureAtlasSprite") && !FMLClientHandler.instance().hasOptifine()) {
            final Feature f = FeatureManager.getFeature(FeatureDisableAnimation.class);
            if (f != null) {
                if (f.enabled) {
                    final ClassNode clazz = ASMUtils.createClassFromByteArray(classBytes);
                    this.transformUpdateAnimation(ASMUtils.getMethodFromClass(clazz, ASMUtils.isSrg ? "func_94219_l" : "updateAnimation", "()V"));
                    return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_MAXS);
                }
            }
        }

        if (transformedName.equals("net.minecraftforge.fml.common.LoadController")) {
            final Feature f = FeatureManager.getFeature(FeatureLoadTimes.class);
            if (f != null) {
                if (f.enabled) {
                    final ClassNode clazz = ASMUtils.createClassFromByteArray(classBytes);
                    this.transformSendEventToModContainer(ASMUtils.getMethodFromClass(clazz, "sendEventToModContainer", "(Lnet/minecraftforge/fml/common/event/FMLEvent;Lnet/minecraftforge/fml/common/ModContainer;)V"));
                    return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_MAXS);
                }
            }
        }


        if (transformedName.equals("net.minecraft.entity.player.EntityPlayerMP")) {
            final Feature f = FeatureManager.getFeature(FeatureBedBug.class);
            if (f != null) {
                if (f.enabled) {
                    final ClassNode clazz = ASMUtils.createClassFromByteArray(classBytes);
                    this.transformWakeUpPlayer(ASMUtils.getMethodFromClass(clazz, ASMUtils.isSrg ? "func_70999_a" : "wakeUpPlayer", "(ZZZ)V"));
                    return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_MAXS);
                }
            }
        }
        return classBytes;
    }

    private void transformWakeUpPlayer (MethodNode method) {

        final InsnList needle = new InsnList();
        needle.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayerMP", "isPlayerSleeping", "()Z", false));
        needle.add(new JumpInsnNode(Opcodes.IFEQ, new LabelNode()));
        needle.add(new LabelNode());
        needle.add(new LineNumberNode(-1, new LabelNode()));
        needle.add(new VarInsnNode(Opcodes.ALOAD, 0));

        ASMUtils.removeNeedleFromHaystack(method.instructions, needle);
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