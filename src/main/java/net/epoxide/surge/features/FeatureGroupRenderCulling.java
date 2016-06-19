package net.epoxide.surge.features;

import java.util.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FeatureGroupRenderCulling extends Feature {

    private boolean enabled;
    private boolean hudRender;
    private int cullThreshold;
    private int cullAmount;

    private Map<UUID, List<UUID>> entityGroup = new WeakHashMap<>();

    @Override
    public void onClientPreInit () {
        if (enabled)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void setupConfig (Configuration config) {
        //TODO Fix
        enabled = config.getBoolean("Group Render Culling", "grouprenderculling", true, "Allows mobs to be culled if there are a lot of them are in the same area");
        hudRender = config.getBoolean("Group Render Culling", "grouprenderculling", true, "Allows mobs to be culled if there are a lot of them are in the same area");
        cullThreshold = config.getInt("Group Render Culling Threshold", "grouprenderculling", 10, 0, 100, "The amount of the same type of entities in a single bounding box being culling");
        cullAmount = config.getInt("Group Render Culling Amount", "grouprenderculling", 10, 1, 100, "The amount of the same type of entities to cull when in a single bounding box");
    }

    @SubscribeEvent
    public void onRenderLiving (RenderLivingEvent.Pre event) {

        this.groupCullEntities(event);
    }

    @SubscribeEvent
    public void onRenderLiving (RenderLivingEvent.Specials.Pre event) {

        this.groupCullEntities(event);
    }

    public void groupCullEntities (RenderLivingEvent event) {
        final EntityLivingBase parentEntity = event.getEntity();

        if ((parentEntity instanceof EntityPlayer))
            return;

        final UUID parentUUID = parentEntity.getUniqueID();
        for (List<UUID> value : entityGroup.values()) {
            if (value.contains(parentUUID)) {
                if (!isWearingArmor(parentEntity))
                    event.setCanceled(true);
                return;
            }
        }

        final List<EntityLivingBase> entityList = parentEntity.getEntityWorld().getEntitiesWithinAABB(parentEntity.getClass(), parentEntity.getRenderBoundingBox());

        if (entityList.size() > cullThreshold) {
            if (entityGroup.containsKey(parentUUID))
                entityGroup.get(parentUUID).clear();
            else
                this.entityGroup.put(parentUUID, new ArrayList<>());

            List<UUID> entityChildren = this.entityGroup.get(parentUUID);
            int i = 0;

            for (Entity e : entityList) {
                i++;
                if (parentUUID == e.getUniqueID())
                    continue;
                if (i >= cullAmount)
                    entityChildren.add(e.getUniqueID());
            }
            if (i - cullAmount > 0 && hudRender) {
                parentEntity.setCustomNameTag("Culled: " + (i - cullAmount));
                parentEntity.setAlwaysRenderNameTag(true);
            }

            entityGroup.put(parentUUID, entityChildren);
        }
        else {
            entityGroup.remove(parentUUID);
            if (hudRender) {
                parentEntity.setCustomNameTag("");
                parentEntity.setAlwaysRenderNameTag(false);
            }
        }
    }

    //DOCME
    //TODO Move to a better location
    public boolean isWearingArmor (EntityLivingBase living) {

        for (final EntityEquipmentSlot slot : EntityEquipmentSlot.values())
            if (slot.getSlotType().equals(EntityEquipmentSlot.Type.ARMOR)) {

                final ItemStack armor = living.getItemStackFromSlot(slot);

                if (armor != null)
                    return true;
            }

        return false;
    }
}