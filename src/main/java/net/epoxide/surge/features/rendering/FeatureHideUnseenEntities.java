
package net.epoxide.surge.features.rendering;

import net.epoxide.surge.features.Feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

public class FeatureHideUnseenEntities extends Feature {

    @Override
    public void onClientPreInit () {

        MinecraftForge.EVENT_BUS.register(this);
    }

//    @SubscribeEvent
//    public void onRenderLiving (RenderLivingEvent.Pre event) {
//
//        this.hideEntities(event);
//    }
//
//    @SubscribeEvent
//    public void onRenderLiving (RenderLivingEvent.Specials.Pre event) {
//
//        this.hideEntities(event);
//    }

    private void hideEntities (RenderLivingEvent event) {

        final EntityLivingBase entity = event.getEntity();
        if (entity instanceof EntityPlayer)
            return;

        final Minecraft mc = Minecraft.getMinecraft();
        final Frustum camera = getCamera(mc.getRenderViewEntity(), mc.getRenderPartialTicks());
        if (!camera.isBoundingBoxInFrustum(entity.getRenderBoundingBox()))
            event.setCanceled(true);
    }

    /**
     * Gets the camera for a specific entity.
     *
     * @param entity The entity to get the camera for.
     * @param partialTicks The partial ticks for the camera.
     * @return The camera for the entity.
     */
    public static Frustum getCamera (Entity entity, float partialTicks) {

        final double cameraX = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
        final double cameraY = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
        final double cameraZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;

        final Frustum camera = new Frustum();
        camera.setPosition(cameraX, cameraY, cameraZ);
        return camera;
    }
}
