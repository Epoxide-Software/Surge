package net.epoxide.surge.client;

import java.util.List;

import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.bookshelf.lib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {
    
    @SubscribeEvent
    public void onRenderLiving (RenderLivingEvent.Pre event) {
        
        this.hideEntities(event);
    }
    
    @SubscribeEvent
    public void onRenderLiving (RenderLivingEvent.Specials.Pre event) {
        
        this.hideEntities(event);
    }
    
    private void hideEntities (RenderLivingEvent event) {
        
        final EntityLivingBase entity = event.getEntity();
        if (!(entity instanceof EntityPlayer)) {
            final Minecraft mc = Minecraft.getMinecraft();
            final Frustum camera = RenderUtils.getCamera(mc.getRenderViewEntity(), mc.getRenderPartialTicks());
            if (!camera.isBoundingBoxInFrustum(entity.getRenderBoundingBox()))
                event.setCanceled(true);
            else {
                final List entityList = entity.getEntityWorld().getEntitiesWithinAABB(entity.getClass(), entity.getRenderBoundingBox());
                if (entityList.size() > 25)
                    if (Constants.RANDOM.nextFloat() <= 0.1f)
                        event.setCanceled(true);
            }
        }
    }
}