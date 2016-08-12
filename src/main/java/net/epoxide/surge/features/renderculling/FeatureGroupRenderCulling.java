package net.epoxide.surge.features.renderculling;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import net.epoxide.surge.features.Feature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FeatureGroupRenderCulling extends Feature {
    
    private boolean hudRender;
    private int cullThreshold;
    private int cullAmount;
    
    private final Map<UUID, Map<UUID, Boolean>> entityGroup = new WeakHashMap<>();
    
    @Override
    public void setupConfig (Configuration config) {
        
        // TODO Fix
        this.hudRender = config.getBoolean("Group Render Culling", "grouprenderculling", true, "Allows mobs to be culled if there are a lot of them are in the same area");
        this.cullThreshold = config.getInt("Group Render Culling Threshold", "grouprenderculling", 10, 0, 100, "The amount of the same type of entities in a single bounding box being culling");
        this.cullAmount = config.getInt("Group Render Culling Amount", "grouprenderculling", 10, 1, 100, "The amount of the same type of entities to cull when in a single bounding box");
    }
    
    // @SubscribeEvent
    // public void onRenderLiving (RenderLivingEvent.Pre event) {
    //
    // this.groupCullEntities(event);
    // }
    //
    // @SubscribeEvent
    // public void onRenderLiving (RenderLivingEvent.Specials.Pre event) {
    //
    // this.groupCullEntities(event);
    // }
    
    public void groupCullEntities (RenderLivingEvent event) {
        
        final EntityLivingBase parentEntity = event.getEntity();
        
        if (parentEntity instanceof EntityPlayer)
            return;
            
        final UUID parentUUID = parentEntity.getUniqueID();
        for (final Map<UUID, Boolean> value : this.entityGroup.values())
            if (value.containsKey(parentUUID)) {
                if (value.get(parentUUID) && !this.isWearingArmor(parentEntity))
                    event.setCanceled(true);
                return;
            }
            
        final List<EntityLivingBase> entityList = parentEntity.getEntityWorld().getEntitiesWithinAABB(parentEntity.getClass(), parentEntity.getEntityBoundingBox());
        
        if (entityList.size() > this.cullThreshold) {
            if (this.entityGroup.containsKey(parentUUID))
                this.entityGroup.get(parentUUID).clear();
            else
                this.entityGroup.put(parentUUID, new WeakHashMap<>());
                
            final Map<UUID, Boolean> entityChildren = this.entityGroup.get(parentUUID);
            int i = 0;
            
            for (final Entity e : entityList) {
                i++;
                if (parentUUID == e.getUniqueID())
                    continue;
                entityChildren.put(e.getUniqueID(), i >= this.cullAmount);
            }
            if (i - this.cullAmount > 0 && this.hudRender) {
                parentEntity.setCustomNameTag("Culled: " + (i - this.cullAmount));
                parentEntity.setAlwaysRenderNameTag(true);
            }
            
            this.entityGroup.put(parentUUID, entityChildren);
        }
        else {
            this.entityGroup.remove(parentUUID);
            if (this.hudRender) {
                parentEntity.setCustomNameTag("");
                parentEntity.setAlwaysRenderNameTag(false);
            }
        }
    }
    
    // DOCME
    // TODO Move to a better location
    public boolean isWearingArmor (EntityLivingBase living) {
        
        for (final EntityEquipmentSlot slot : EntityEquipmentSlot.values())
            if (slot.getSlotType().equals(EntityEquipmentSlot.Type.ARMOR)) {
                
                final ItemStack armor = living.getItemStackFromSlot(slot);
                
                if (armor != null)
                    return true;
            }
            
        return false;
    }
    
    @Override
    public boolean usesEvents () {
        
        return true;
    }
}