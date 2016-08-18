package net.epoxide.surge.features.renderculling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.epoxide.surge.asm.ASMUtils;
import net.epoxide.surge.asm.mappings.MethodMapping;
import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.features.Feature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FeatureGroupRenderCulling extends Feature {
    
    private static String CLASS_RENDER_MANAGER = "net.minecraft.client.renderer.entity.RenderManager";
    private static MethodMapping METHOD_DO_RENDER_ENTITY = new MethodMapping("func_188391_a", "doRenderEntity", void.class, Entity.class, double.class, double.class, double.class, float.class, float.class, boolean.class);
    private static int cullThreshold;
    
    private static boolean shouldCull;
    
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
        
        shouldCull = !shouldCull;
        
        // TODO remove
        for (final EntityLivingBase entityLivingBase : parentMap.keySet()) {
            
            entityLivingBase.setCustomNameTag("");
            entityLivingBase.setAlwaysRenderNameTag(false);
        }
        
        parentMap.clear();
    }
    
    public static boolean shouldRenderCull () {
        
        return shouldCull;
    }
    
    public static boolean shouldRender (Entity entity) {
        
        if (shouldCull) {
            
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
    
    @Override
    public void readNBT (NBTTagCompound nbt) {
        
        shouldCull = nbt.getBoolean("shouldCull");
    }
    
    @Override
    public void writeNBT (NBTTagCompound nbt) {
        
        nbt.setBoolean("shouldCull", shouldCull);
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