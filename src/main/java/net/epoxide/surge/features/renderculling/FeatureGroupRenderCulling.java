package net.epoxide.surge.features.renderculling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.epoxide.surge.command.CommandSurgeWrapper;
import net.epoxide.surge.features.Feature;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FeatureGroupRenderCulling extends Feature {

    private int cullThreshold;

    private static boolean renderCull;

    private static final Map<EntityLivingBase, List<EntityLivingBase>> parentMap = new WeakHashMap<>();
    private List<EntityLivingBase> cullList = new ArrayList<>();

    @Override
    public void setupConfig (Configuration config) {

        // TODO Fix
        this.cullThreshold = config.getInt("Group Render Culling Threshold", "grouprenderculling", 10, 0, 100, "The amount of the same type of entities in a single bounding box being culling");
    }

    @Override
    public void onInit () {

        CommandSurgeWrapper.addCommand(new CommandGroupRenderCulling());
    }

    @SubscribeEvent
    public void onRenderLiving (RenderLivingEvent event) {

        if (renderCull) {

            EntityLivingBase currentEntity = event.getEntity();
            if (currentEntity instanceof EntityPlayer)
                return;

            if (cullList.contains(currentEntity)) {
                event.setCanceled(true);
            }
            else if (parentMap.containsKey(currentEntity)) {
                final List<EntityLivingBase> entityList = currentEntity.getEntityWorld().getEntitiesWithinAABB(currentEntity.getClass(), currentEntity.getEntityBoundingBox());

                entityList.remove(currentEntity);

                List<EntityLivingBase> childMap = this.parentMap.get(currentEntity);
                this.cullList.removeAll(childMap);

                childMap.clear();
                if (entityList.size() > cullThreshold) {
                    childMap.addAll(entityList);
                    this.cullList.addAll(entityList);
                    currentEntity.setCustomNameTag("Culled: " + entityList.size());
                    currentEntity.setAlwaysRenderNameTag(true);
                }
                else {
                    this.parentMap.remove(currentEntity);
                }

            }
            else if (!parentMap.containsKey(currentEntity)) {
                final List<EntityLivingBase> entityList = currentEntity.getEntityWorld().getEntitiesWithinAABB(currentEntity.getClass(), currentEntity.getEntityBoundingBox());

                entityList.remove(currentEntity);

                List<EntityLivingBase> childMap = new ArrayList<>();

                if (entityList.size() > cullThreshold) {
                    childMap.addAll(entityList);
                    this.cullList.addAll(entityList);
                    currentEntity.setCustomNameTag("Culled: " + entityList.size());
                    currentEntity.setAlwaysRenderNameTag(true);
                    this.parentMap.put(currentEntity, childMap);
                }
            }
        }
    }

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

    public static void toggleRenderCull () {

        renderCull = !renderCull;

        for (EntityLivingBase entityLivingBase : parentMap.keySet()) {

            entityLivingBase.setCustomNameTag("");
            entityLivingBase.setAlwaysRenderNameTag(false);
        }

        parentMap.clear();
    }

    public static boolean shouldRenderCull () {

        return renderCull;
    }
}