package net.epoxide.surge.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
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

import net.epoxide.surge.asm.mappings.FieldMapping;
import net.epoxide.surge.asm.mappings.Mapping;
import net.minecraft.launchwrapper.IClassTransformer;

public class SurgeTransformerManager implements IClassTransformer {
    private final String CLASS_MINECRAFT;
    private final String CLASS_RENDER_GLOBAL;
    private final String CLASS_RENDER_MANAGER;
    private final String CLASS_LOAD_CONTROLLER;
    private final String CLASS_TEXTURE_ATLAS_SPRITE;
    
    private final Mapping METHOD_RENDER_CLOUDS;
    private final Mapping METHOD_DO_RENDER_ENTITY;
    private final Mapping METHOD_UPDATE_ANIMATION;
    private final Mapping METHOD_SEND_EVENT_TO_MOD_CONTAINER;
    
    private final FieldMapping FIELD_RENDERGLOBAL_MC;
    private final FieldMapping FIELD_MINECRAFT_THEWORLD;
    private final FieldMapping FIELD_RENDERGLOBAL_CLOUDTICKCOUNTER;
    
    public SurgeTransformerManager() {
        
        this.CLASS_MINECRAFT = "net.minecraft.client.Minecraft";
        this.CLASS_RENDER_GLOBAL = "net.minecraft.client.renderer.RenderGlobal";
        this.CLASS_RENDER_MANAGER = "net.minecraft.client.renderer.entity.RenderManager";
        this.CLASS_LOAD_CONTROLLER = "net.minecraftforge.fml.common.LoadController";
        this.CLASS_TEXTURE_ATLAS_SPRITE = "net.minecraft.client.renderer.texture.TextureAtlasSprite";
        
        this.METHOD_RENDER_CLOUDS = new Mapping("func_180447_b", "renderClouds", "(FI)V");
        this.METHOD_UPDATE_ANIMATION = new Mapping("func_94219_l", "updateAnimation", "()V");
        this.METHOD_DO_RENDER_ENTITY = new Mapping("func_188391_a", "doRenderEntity", "(Lnet/minecraft/entity/Entity;DDDFFZ)V");
        this.METHOD_SEND_EVENT_TO_MOD_CONTAINER = new Mapping("sendEventToModContainer", "(Lnet/minecraftforge/fml/common/event/FMLEvent;Lnet/minecraftforge/fml/common/ModContainer;)V");
        
        this.FIELD_RENDERGLOBAL_MC = new FieldMapping(this.CLASS_RENDER_GLOBAL, "field_72777_q", "mc", "Lnet/minecraft/client/Minecraft;");
        this.FIELD_MINECRAFT_THEWORLD = new FieldMapping(this.CLASS_MINECRAFT, "field_71441_e", "theWorld", "Lnet/minecraft/client/multiplayer/WorldClient;");
        this.FIELD_RENDERGLOBAL_CLOUDTICKCOUNTER = new FieldMapping(this.CLASS_RENDER_GLOBAL, "field_72773_u", "cloudTickCounter", "I");
    }
    
    @Override
    public byte[] transform (String name, String transformedName, byte[] classBytes) {
        
        if (transformedName.equals(this.CLASS_RENDER_GLOBAL)) {
            final ClassNode clazz = ASMUtils.createClassFromByteArray(classBytes);
            this.transformRenderClouds(this.METHOD_RENDER_CLOUDS.getMethodNode(clazz));
            return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_MAXS);
        }
        else if (transformedName.equals(this.CLASS_RENDER_MANAGER)) {
            final ClassNode clazz = ASMUtils.createClassFromByteArray(classBytes);
            this.transformDoRenderEntity(this.METHOD_DO_RENDER_ENTITY.getMethodNode(clazz));
            return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_MAXS);
        }
        else if (transformedName.equals(this.CLASS_LOAD_CONTROLLER)) {
            final ClassNode clazz = ASMUtils.createClassFromByteArray(classBytes);
            this.transformSendEventToModContainer(this.METHOD_SEND_EVENT_TO_MOD_CONTAINER.getMethodNode(clazz));
            return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_MAXS);
        }
        else if (transformedName.equals(this.CLASS_TEXTURE_ATLAS_SPRITE)) {
            final ClassNode clazz = ASMUtils.createClassFromByteArray(classBytes);
            this.transformUpdateAnimation(this.METHOD_UPDATE_ANIMATION.getMethodNode(clazz));
            return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_MAXS);
        }
        return classBytes;
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
     * Transforms the doRenderEntity method to allow greater control over rendering.
     *
     * @param method RenderManager#doRenderEntity
     */
    private void transformDoRenderEntity (MethodNode method) {
        
        final InsnList newInstr = new InsnList();
        newInstr.add(new VarInsnNode(Opcodes.ALOAD, 1));
        
        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/epoxide/surge/features/renderculling/FeatureGroupRenderCulling", "shouldRender", "(Lnet/minecraft/entity/Entity;)Z", false));
        final LabelNode label = new LabelNode();
        newInstr.add(new JumpInsnNode(Opcodes.IFNE, label));
        newInstr.add(new LabelNode());
        newInstr.add(new InsnNode(Opcodes.RETURN));
        newInstr.add(label);
        
        method.instructions.insert(method.instructions.getFirst(), newInstr);
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
            newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/epoxide/surge/features/loadtime/FeatureLoadTimes", "registerLoadingTime", "(Lnet/minecraftforge/fml/common/ModContainer;Lnet/minecraftforge/fml/common/event/FMLEvent;JJ)V", false));
            newInstr.add(new LabelNode());
            
            method.instructions.insert(pointer, newInstr);
        }
    }
    
    /**
     * Transforms the update animation method to check our custom hook before updating. Allows
     * animation to be disabled.
     *
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
}