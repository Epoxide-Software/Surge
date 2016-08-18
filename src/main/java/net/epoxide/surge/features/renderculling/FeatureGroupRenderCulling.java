package net.epoxide.surge.features.renderculling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.epoxide.surge.asm.ASMUtils;
import net.epoxide.surge.asm.mappings.MethodMapping;
import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.features.Feature;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

@SideOnly(Side.CLIENT)
public class FeatureGroupRenderCulling extends Feature {
    
    private static String CLASS_RENDER_MANAGER = "net.minecraft.client.renderer.entity.RenderManager";
    private static MethodMapping METHOD_DO_RENDER_ENTITY = new MethodMapping("func_188391_a", "doRenderEntity", void.class, Entity.class, double.class, double.class, double.class, float.class, float.class, boolean.class);
    private static int cullThreshold;
    
    private static boolean renderCull;
    
    private static final Map<EntityLivingBase, List<EntityLivingBase>> parentMap = new WeakHashMap<>();
    private static List<EntityLivingBase> cullList = new ArrayList<>();
    
    @Override
    public void setupConfig (Configuration config) {
        
        // TODO Fix
        FeatureGroupRenderCulling.cullThreshold = config.getInt("Group Render Culling Threshold", "grouprenderculling", 10, 0, 100, "The amount of the same type of entities in a single bounding box being culling");
    }
    
    @Override
    public void onInit () {
        
        CommandSurgeWrapper.addCommand(new CommandGroupRenderCulling());
    }
    
    @Override
    public boolean usesEvents () {
        
        return true;
    }
    
    public static void toggleRenderCull () {
        
        renderCull = !renderCull;
        
        for (final EntityLivingBase entityLivingBase : parentMap.keySet()) {
            
            entityLivingBase.setCustomNameTag("");
            entityLivingBase.setAlwaysRenderNameTag(false);
        }
        
        parentMap.clear();
    }
    
    public static boolean shouldRenderCull () {
        
        return renderCull;
    }
    
    public static boolean shouldRender (Entity entity) {
        
        if (renderCull) {

            if (entity instanceof EntityPlayer || !(entity instanceof EntityLivingBase))
                return true;

            final EntityLivingBase living = (EntityLivingBase) entity;
            if (cullList.contains(living))
                return false;
            else if (parentMap.containsKey(living)) {
                final List<EntityLivingBase> entityList = living.getEntityWorld().getEntitiesWithinAABB(living.getClass(), living.getEntityBoundingBox());
                
                entityList.remove(living);
                
                final List<EntityLivingBase> childMap = parentMap.get(living);
                cullList.removeAll(childMap);
                
                childMap.clear();
                if (entityList.size() > cullThreshold) {
                    childMap.addAll(entityList);
                    cullList.addAll(entityList);
                    living.setCustomNameTag("Culled: " + entityList.size());
                    living.setAlwaysRenderNameTag(true);
                }
                else
                    parentMap.remove(living);

            }
            else if (!parentMap.containsKey(living)) {
                final List<EntityLivingBase> entityList = living.getEntityWorld().getEntitiesWithinAABB(living.getClass(), living.getEntityBoundingBox());
                
                entityList.remove(living);
                
                final List<EntityLivingBase> childMap = new ArrayList<>();
                
                if (entityList.size() > cullThreshold) {
                    childMap.addAll(entityList);
                    cullList.addAll(entityList);
                    living.setCustomNameTag("Culled: " + entityList.size());
                    living.setAlwaysRenderNameTag(true);
                    parentMap.put(living, childMap);
                }
            }
        }
        return true;
    }
    
    @Override
    public byte[] transform (String name, String transformedName, byte[] bytes) {
        
        final ClassNode clazz = ASMUtils.createClassFromByteArray(bytes);
        final MethodNode method = METHOD_DO_RENDER_ENTITY.getMethodNode(clazz);
        this.transformDoRenderEntity(method);
        return ASMUtils.createByteArrayFromClass(clazz, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }

    private void transformDoRenderEntity (MethodNode method) {
        final InsnList needle = new InsnList();
        needle.add(new LabelNode());
        needle.add(new LineNumberNode(-1, new LabelNode()));

        final AbstractInsnNode pointer = ASMUtils.findLastNodeFromNeedle(method.instructions, needle);

        final InsnList newInstr = new InsnList();
        newInstr.add(new VarInsnNode(Opcodes.ALOAD, 1));
        newInstr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/epoxide/surge/features/renderculling/FeatureGroupRenderCulling", "shouldRender", "(Lnet/minecraft/entity/Entity;)Z", false));
        final LabelNode L13 = new LabelNode();
        newInstr.add(new JumpInsnNode(Opcodes.IFNE, L13));
        newInstr.add(new LabelNode());
        newInstr.add(new InsnNode(Opcodes.RETURN));
        newInstr.add(L13);

        method.instructions.insert(pointer, newInstr);
    }
    
    @Override
    public boolean isTransformer () {
        
        return true;
    }
    
    @Override
    public boolean shouldTransform (String name) {
        
        return CLASS_RENDER_MANAGER.equals(name);
    }
}