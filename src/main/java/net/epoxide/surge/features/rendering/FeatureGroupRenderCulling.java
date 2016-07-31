package net.epoxide.surge.features.rendering;

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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FeatureGroupRenderCulling extends Feature {

    private boolean enabled;
    private boolean hudRender;
    private int cullThreshold;
    private int cullAmount;

    private Map<UUID, Map<UUID, Boolean>> entityGroup = new WeakHashMap<>();

    @Override
    public void onClientPreInit () {
        if (this.enabled)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void setupConfig (Configuration config) {
        //TODO Fix
        this.enabled = config.getBoolean("Group Render Culling", "grouprenderculling", true, "Allows mobs to be culled if there are a lot of them are in the same area");
        this.hudRender = config.getBoolean("Group Render Culling", "grouprenderculling", true, "Allows mobs to be culled if there are a lot of them are in the same area");
        this.cullThreshold = config.getInt("Group Render Culling Threshold", "grouprenderculling", 10, 0, 100, "The amount of the same type of entities in a single bounding box being culling");
        this.cullAmount = config.getInt("Group Render Culling Amount", "grouprenderculling", 10, 1, 100, "The amount of the same type of entities to cull when in a single bounding box");
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
        for (Map<UUID, Boolean> value : entityGroup.values()) {
            if (value.containsKey(parentUUID)) {
                if (value.get(parentUUID) && !isWearingArmor(parentEntity))
                    event.setCanceled(true);
                return;
            }
        }

        final List<EntityLivingBase> entityList = parentEntity.getEntityWorld().getEntitiesWithinAABB(parentEntity.getClass(), parentEntity.getEntityBoundingBox());

        if (entityList.size() > cullThreshold) {
            if (entityGroup.containsKey(parentUUID))
                entityGroup.get(parentUUID).clear();
            else
                this.entityGroup.put(parentUUID, new WeakHashMap<>());

            Map<UUID, Boolean> entityChildren = this.entityGroup.get(parentUUID);
            int i = 0;

            for (Entity e : entityList) {
                i++;
                if (parentUUID == e.getUniqueID())
                    continue;
                entityChildren.put(e.getUniqueID(), i >= cullAmount);
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