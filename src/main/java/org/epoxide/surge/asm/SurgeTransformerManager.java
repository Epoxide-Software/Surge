package org.epoxide.surge.asm;

import java.util.Map;

import org.epoxide.surge.features.Feature;
import org.epoxide.surge.features.FeatureManager;
import org.epoxide.surge.features.animation.FeatureDisableAnimation;
import org.epoxide.surge.features.gpucloud.FeatureGPUClouds;
import org.epoxide.surge.features.loadtime.FeatureLoadTimes;

import com.google.common.collect.ImmutableMap;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModMetadata;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class SurgeTransformerManager implements IClassTransformer {

    @Override
    public byte[] transform (String name, String transformedName, byte[] classBytes) {

        if (transformedName.equals("net.minecraft.client.renderer.texture.TextureAtlasSprite") && !hasOptifine()) {
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

        if (transformedName.equals("net.minecraft.client.renderer.RenderGlobal") && !hasOptifine()) {
            final Feature f = FeatureManager.getFeature(FeatureGPUClouds.class);
            if (f != null) {
                if (f.enabled) {
                    final ClassNode clazz = ASMUtils.createClassFromByteArray(classBytes);
                    this.transformRenderClouds(ASMUtils.getMethodFromClass(clazz, ASMUtils.isSrg ? "func_180447_b" : "renderClouds", "(FI)V"));
                    return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_MAXS);
                }
            }
        }
        return classBytes;
    }

    public boolean hasOptifine () {

        try {
            Class<?> optifineConfig = Class.forName("Config", false, Loader.instance().getModClassLoader());
            String optifineVersion = (String) optifineConfig.getField("VERSION").get(null);
            Map<String, Object> dummyOptifineMeta = ImmutableMap.<String, Object>builder().put("name", "Optifine").put("version", optifineVersion).build();
            ModMetadata optifineMetadata = MetadataCollection.from(getClass().getResourceAsStream("optifinemod.info"), "optifine").getMetadataForId("optifine", dummyOptifineMeta);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Transforms the renderClouds method to take gpu cloud rendering into account.
     *
     * @param method RenderGlobal#renderClouds
     */
    private void transformRenderClouds (MethodNode method) {

        final InsnList needle = new InsnList();
        needle.add(new VarInsnNode(Opcodes.ALOAD, 0));
        needle.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/RenderGlobal", ASMUtils.isSrg ? "field_72777_q" : "mc", "Lnet/minecraft/client/Minecraft;"));
        needle.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", ASMUtils.isSrg ? "field_71441_e" : "theWorld", "Lnet/minecraft/client/multiplayer/WorldClient;"));
        needle.add(new VarInsnNode(Opcodes.ALOAD, 0));
        needle.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/RenderGlobal", ASMUtils.isSrg ? "field_72777_q" : "mc", "Lnet/minecraft/client/Minecraft;"));
        needle.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/IRenderHandler", "render", "(FLnet/minecraft/client/multiplayer/WorldClient;Lnet/minecraft/client/Minecraft;)V", false));
        needle.add(new LabelNode());
        needle.add(new LineNumberNode(-1, new LabelNode()));
        needle.add(new InsnNode(Opcodes.RETURN));
        needle.add(new LabelNode());
        needle.add(new LineNumberNode(-1, new LabelNode()));

        final AbstractInsnNode pointer = ASMUtils.findLastNodeFromNeedle(method.instructions, needle);
        final InsnList newInstr = new InsnList();

        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/epoxide/surge/features/gpucloud/FeatureGPUClouds", "getInstance", "()Lorg/epoxide/surge/features/gpucloud/CloudRenderer;", false));
        newInstr.add(new VarInsnNode(Opcodes.FLOAD, 1));
        newInstr.add(new VarInsnNode(Opcodes.ALOAD, 0));
        newInstr.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/RenderGlobal", ASMUtils.isSrg ? "field_72773_u" : "cloudTickCounter", "I"));
        newInstr.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "org/epoxide/surge/features/gpucloud/CloudRenderer", "render", "(FI)Z", false));
        final LabelNode label5 = new LabelNode();
        newInstr.add(new JumpInsnNode(Opcodes.IFEQ, label5));
        newInstr.add(new InsnNode(Opcodes.RETURN));
        newInstr.add(label5);

        method.instructions.insert(pointer, newInstr);
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