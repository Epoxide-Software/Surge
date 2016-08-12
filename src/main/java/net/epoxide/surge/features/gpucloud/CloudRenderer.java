package net.epoxide.surge.features.gpucloud;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author Zaggy1024
 */
public class CloudRenderer implements IResourceManagerReloadListener {
    
    // Shared constants.
    private static final float PX_SIZE = 1 / 256F;
    
    // Building constants.
    private static final VertexFormat FORMAT = DefaultVertexFormats.POSITION_TEX_COLOR;
    private static final int HEIGHT = 4;
    private static final int FULL_WIDTH = 64;
    private static final int START = -FULL_WIDTH / 2;
    private static final int END = FULL_WIDTH / 2;
    private static final int SECTION_WIDTH = 8;
    private static final float INSET = 0.001F;
    private static final float ALPHA = 0.8F;
    
    private static Minecraft MC;
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/environment/clouds.png");
    
    private int ticks = 0;
    
    private int displayList = -1;
    private net.minecraft.client.renderer.vertex.VertexBuffer vbo;
    private int cloudMode = -1;
    
    private final DynamicTexture COLOR_TEX;
    
    private int texW;
    private int texH;
    
    public CloudRenderer() {
        
        MC = Minecraft.getMinecraft();
        MinecraftForge.EVENT_BUS.register(this);
        final IResourceManager resourceManager = MC.getResourceManager();
        ((IReloadableResourceManager) resourceManager).registerReloadListener(this);
        this.COLOR_TEX = new DynamicTexture(1, 1);
    }
    
    private int getScale () {
        
        return this.cloudMode == 2 ? 12 : 8;
    }
    
    private void vertices (VertexBuffer buffer) {
        
        final boolean fancy = this.cloudMode == 2;
        final float scale = this.getScale();
        final float cullDist = 2 * scale;
        final float bCol = fancy ? 0.7F : 1F;
        
        buffer.begin(GL11.GL_QUADS, FORMAT);
        
        // Create 1200 quads (with SECTION_WIDTH 8). SECTION_WIDTH defines width of slices
        // and vertical faces. With the minimum number (built above), the clouds nearest
        // the player will still be fully fogged.
        final float sectStart = START * scale;
        final float sectEnd = END * scale;
        final float sectStep = SECTION_WIDTH * scale;
        final float sectPx = PX_SIZE / scale;
        
        float sectX0 = sectStart;
        float sectX1;
        
        for (sectX1 = sectStart + sectStep; sectX1 <= sectEnd; sectX1 += sectStep) {
            
            if (Float.isNaN(sectX0)) {
                
                sectX0 = sectX1;
                continue;
            }
            
            float sectZ0 = sectStart;
            float sectZ1;
            
            for (sectZ1 = sectStart + sectStep; sectZ1 <= sectEnd; sectZ1 += sectStep) {
                
                final float u0 = sectX0 * sectPx;
                final float u1 = sectX1 * sectPx;
                final float v0 = sectZ0 * sectPx;
                final float v1 = sectZ1 * sectPx;
                
                // Bottom
                buffer.pos(sectX0, 0, sectZ0).tex(u0, v0).color(bCol, bCol, bCol, ALPHA).endVertex();
                buffer.pos(sectX1, 0, sectZ0).tex(u1, v0).color(bCol, bCol, bCol, ALPHA).endVertex();
                buffer.pos(sectX1, 0, sectZ1).tex(u1, v1).color(bCol, bCol, bCol, ALPHA).endVertex();
                buffer.pos(sectX0, 0, sectZ1).tex(u0, v1).color(bCol, bCol, bCol, ALPHA).endVertex();
                
                if (fancy) {
                    
                    // Top
                    buffer.pos(sectX0, HEIGHT, sectZ0).tex(u0, v0).color(1, 1, 1, ALPHA).endVertex();
                    buffer.pos(sectX0, HEIGHT, sectZ1).tex(u0, v1).color(1, 1, 1, ALPHA).endVertex();
                    buffer.pos(sectX1, HEIGHT, sectZ1).tex(u1, v1).color(1, 1, 1, ALPHA).endVertex();
                    buffer.pos(sectX1, HEIGHT, sectZ0).tex(u1, v0).color(1, 1, 1, ALPHA).endVertex();
                    
                    float slice;
                    float sliceCoord0;
                    float sliceCoord1;
                    
                    for (slice = sectX0; slice < sectX1;) {
                        
                        sliceCoord0 = slice * sectPx;
                        sliceCoord1 = sliceCoord0 + PX_SIZE;
                        
                        // X sides
                        if (slice > -cullDist) {
                            
                            slice += INSET;
                            buffer.pos(slice, 0, sectZ1).tex(sliceCoord0, v1).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, HEIGHT, sectZ1).tex(sliceCoord1, v1).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, HEIGHT, sectZ0).tex(sliceCoord1, v0).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, 0, sectZ0).tex(sliceCoord0, v0).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            slice -= INSET;
                        }
                        
                        slice += scale;
                        
                        if (slice <= cullDist) {
                            
                            slice -= INSET;
                            buffer.pos(slice, 0, sectZ0).tex(sliceCoord0, v0).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, HEIGHT, sectZ0).tex(sliceCoord1, v0).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, HEIGHT, sectZ1).tex(sliceCoord1, v1).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, 0, sectZ1).tex(sliceCoord0, v1).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            slice += INSET;
                        }
                    }
                    
                    for (slice = sectZ0; slice < sectZ1;) {
                        
                        sliceCoord0 = slice * sectPx;
                        sliceCoord1 = sliceCoord0 + PX_SIZE;
                        
                        // Z sides
                        if (slice > -cullDist) {
                            
                            slice += INSET;
                            buffer.pos(sectX0, 0, slice).tex(u0, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX0, HEIGHT, slice).tex(u0, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX1, HEIGHT, slice).tex(u1, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX1, 0, slice).tex(u1, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            slice -= INSET;
                        }
                        
                        slice += scale;
                        
                        if (slice <= cullDist) {
                            
                            slice -= INSET;
                            buffer.pos(sectX1, 0, slice).tex(u1, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX1, HEIGHT, slice).tex(u1, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX0, HEIGHT, slice).tex(u0, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX0, 0, slice).tex(u0, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            slice += INSET;
                        }
                    }
                }
                
                sectZ0 = sectZ1;
            }
            
            sectX0 = sectX1;
        }
    }
    
    private void rebuild () {
        
        if (this.vbo != null)
            this.vbo.deleteGlBuffers();
            
        if (this.displayList >= 0) {
            
            GLAllocation.deleteDisplayLists(this.displayList);
            this.displayList = -1;
        }
        
        if (MC.gameSettings.shouldRenderClouds() != 0) {
            
            final Tessellator tess = Tessellator.getInstance();
            final VertexBuffer buffer = tess.getBuffer();
            
            if (OpenGlHelper.useVbo())
                this.vbo = new net.minecraft.client.renderer.vertex.VertexBuffer(FORMAT);
                
            else
                GlStateManager.glNewList(this.displayList = GLAllocation.generateDisplayLists(1), GL11.GL_COMPILE);
                
            this.vertices(buffer);
            
            if (OpenGlHelper.useVbo()) {
                
                buffer.finishDrawing();
                buffer.reset();
                this.vbo.bufferData(buffer.getByteBuffer());
            }
            
            else {
                
                tess.draw();
                GlStateManager.glEndList();
            }
        }
    }
    
    private int fullCoord (double coord, int scale) {
        
        return (int) coord / scale - (coord < 0 ? 1 : 0);
    }
    
    @SubscribeEvent
    public void onTick (TickEvent.ClientTickEvent event) {
        
        if (event.phase == TickEvent.Phase.START && !MC.isGamePaused())
            this.ticks++;
    }
    
    public boolean render (float partialTicks) {
        
        if (!MC.theWorld.provider.isSurfaceWorld())
            return true;
            
        if (this.cloudMode != MC.gameSettings.shouldRenderClouds() || (OpenGlHelper.useVbo() ? this.vbo == null : this.displayList < 0)) {
            
            this.cloudMode = MC.gameSettings.shouldRenderClouds();
            this.rebuild();
        }
        
        final Entity entity = MC.getRenderViewEntity();       
        final double totalOffset = this.ticks + partialTicks;       
        final double x = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks + totalOffset * 0.03;
        final double y = MC.theWorld.provider.getCloudHeight() - (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks) + 0.33;
        double z = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
        
        if (this.cloudMode == 2)
            z += 0.33 * this.getScale();
            
        final int scale = this.getScale();
        
        // Integer UVs to translate the texture matrix by.
        int offU = this.fullCoord(x, scale);
        int offV = this.fullCoord(z, scale);
        
        GlStateManager.pushMatrix();
        
        // Translate by the remainder after the UV offset.
        GlStateManager.translate(offU * scale - x, y, offV * scale - z);
        
        // Modulo to prevent texture samples becoming inaccurate at extreme offsets.
        offU = Math.floorMod(offU, this.texW);
        offV = Math.floorMod(offV, this.texH);
        
        // Translate the texture.
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.translate(offU * PX_SIZE, offV * PX_SIZE, 0);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        
        GlStateManager.disableCull();
        
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        
        // Color multiplier.
        final Vec3d color = MC.theWorld.getCloudColour(partialTicks);
        float r = (float) color.xCoord;
        float g = (float) color.yCoord;
        float b = (float) color.zCoord;
        
        if (MC.gameSettings.anaglyph) {
            
            final float tempR = r * 0.3F + g * 0.59F + b * 0.11F;
            final float tempG = r * 0.3F + g * 0.7F;
            final float tempB = r * 0.3F + b * 0.7F;
            r = tempR;
            g = tempG;
            b = tempB;
        }
        
        // Apply a color multiplier through a texture upload if shaders aren't supported.
        this.COLOR_TEX.getTextureData()[0] = 255 << 24 | (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255);
        this.COLOR_TEX.updateDynamicTexture();
        
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.bindTexture(this.COLOR_TEX.getGlTextureId());
        GlStateManager.enableTexture2D();
        
        // Bind the clouds texture last so the shader's sampler2D is correct.
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        MC.renderEngine.bindTexture(TEXTURE);
        
        final ByteBuffer buffer = Tessellator.getInstance().getBuffer().getByteBuffer();
        
        // Set up pointers for the display list/VBO.
        if (OpenGlHelper.useVbo()) {
            
            this.vbo.bindBuffer();
            
            final int stride = FORMAT.getNextOffset();
            GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, stride, 0);
            GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, stride, 12);
            GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, stride, 20);
            GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
        }
        
        else {
            
            buffer.limit(FORMAT.getNextOffset());
            
            for (int i = 0; i < FORMAT.getElementCount(); i++)
                FORMAT.getElements().get(i).getUsage().preDraw(FORMAT, i, FORMAT.getNextOffset(), buffer);
            
            buffer.position(0);
        }
        
        // Depth pass to prevent insides rendering from the outside.
        GlStateManager.colorMask(false, false, false, false);
        
        if (OpenGlHelper.useVbo())
            this.vbo.drawArrays(GL11.GL_QUADS);
        
        else
            GlStateManager.callList(this.displayList);
            
        // Full render.
        if (!MC.gameSettings.anaglyph)
            GlStateManager.colorMask(true, true, true, true);
        
        else
            switch (EntityRenderer.anaglyphField) {
                
                case 0:
                    GlStateManager.colorMask(false, true, true, true);
                    break;
                    
                case 1:
                    GlStateManager.colorMask(true, false, false, true);
                    break;
            }
        
        if (OpenGlHelper.useVbo())
            this.vbo.drawArrays(GL11.GL_QUADS);
        
        else
            GlStateManager.callList(this.displayList);
            
        // Unbind buffer and disable pointers.
        if (OpenGlHelper.useVbo())
            this.vbo.unbindBuffer();
            
        buffer.limit(0);
        
        for (int i = 0; i < FORMAT.getElementCount(); i++)
            FORMAT.getElements().get(i).getUsage().postDraw(FORMAT, i, FORMAT.getNextOffset(), buffer);
        
        buffer.position(0);
        
        // Disable our coloring.
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        
        // Reset texture matrix.
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        
        GlStateManager.popMatrix();
        
        return true;
    }
    
    private void reloadTextures () {
        
        MC.renderEngine.bindTexture(TEXTURE);
        this.texW = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        this.texH = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
    }
    
    @Override
    public void onResourceManagerReload (IResourceManager resourceManager) {
        
        this.reloadTextures();
    }
}