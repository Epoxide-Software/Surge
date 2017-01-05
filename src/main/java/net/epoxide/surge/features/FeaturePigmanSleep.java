package net.epoxide.surge.features;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FeaturePigmanSleep extends Feature {

    @Override
    public void onPreInit () {

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onSleepEvent (PlayerSleepInBedEvent event) {

        EntityPlayer entityPlayer = event.getEntityPlayer();
        if (!entityPlayer.worldObj.isRemote) {
            if (entityPlayer.isPlayerSleeping() || !entityPlayer.isEntityAlive()) {
                event.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
                return;
            }

            if (!entityPlayer.worldObj.provider.isSurfaceWorld()) {
                event.setResult(EntityPlayer.SleepResult.NOT_POSSIBLE_HERE);
                return;
            }

            if (entityPlayer.worldObj.isDaytime()) {
                event.setResult(EntityPlayer.SleepResult.NOT_POSSIBLE_NOW);
                return;
            }

            if (Math.abs(entityPlayer.posX - (double) event.getPos().getX()) > 3.0D || Math.abs(entityPlayer.posY - (double) event.getPos().getY()) > 2.0D || Math.abs(entityPlayer.posZ - (double) event.getPos().getZ()) > 3.0D) {
                event.setResult(EntityPlayer.SleepResult.TOO_FAR_AWAY);
                return;
            }

            double d0 = 8.0D;
            double d1 = 5.0D;
            List<EntityMob> list = entityPlayer.worldObj.<EntityMob>getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB((double) event.getPos().getX() - 8.0D, (double) event.getPos().getY() - 5.0D, (double) event.getPos().getZ() - 8.0D, (double) event.getPos().getX() + 8.0D, (double) event.getPos().getY() + 5.0D, (double) event.getPos().getZ() + 8.0D));
            List<EntityMob> mobList = new ArrayList<>();
            for (EntityMob mob : list) {
                if (mob instanceof EntityPigZombie) {
                    if (((EntityPigZombie) mob).angerTargetUUID != entityPlayer.getUniqueID())
                        continue;
                }
                mobList.add(mob);
            }
            if (!mobList.isEmpty()) {
                event.setResult(EntityPlayer.SleepResult.NOT_SAFE);
                return;
            }
        }

        if (entityPlayer.isRiding()) {
            entityPlayer.dismountRidingEntity();
        }
        try {
            Method m = Entity.class.getDeclaredMethod("setSize", float.class, float.class);
            m.setAccessible(true);
            m.invoke(entityPlayer, 0.2f, 0.2f);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        IBlockState state = null;
        if (entityPlayer.worldObj.isBlockLoaded(event.getPos()))
            state = entityPlayer.worldObj.getBlockState(event.getPos());
        if (state != null && state.getBlock().isBed(state, entityPlayer.worldObj, event.getPos(), entityPlayer)) {
            EnumFacing enumfacing = state.getBlock().getBedDirection(state, entityPlayer.worldObj, event.getPos());
            float f = 0.5F;
            float f1 = 0.5F;

            switch (enumfacing) {
                case SOUTH:
                    f1 = 0.9F;
                    break;
                case NORTH:
                    f1 = 0.1F;
                    break;
                case WEST:
                    f = 0.1F;
                    break;
                case EAST:
                    f = 0.9F;
            }

            entityPlayer.setRenderOffsetForSleep(enumfacing);
            entityPlayer.setPosition((double) ((float) event.getPos().getX() + f), (double) ((float) event.getPos().getY() + 0.6875F), (double) ((float) event.getPos().getZ() + f1));
        }
        else {
            entityPlayer.setPosition((double) ((float) event.getPos().getX() + 0.5F), (double) ((float) event.getPos().getY() + 0.6875F), (double) ((float) event.getPos().getZ() + 0.5F));
        }

        entityPlayer.sleeping = true;
        entityPlayer.sleepTimer = 0;
        entityPlayer.bedLocation = event.getPos();
        entityPlayer.motionX = 0.0D;
        entityPlayer.motionY = 0.0D;
        entityPlayer.motionZ = 0.0D;

        if (!entityPlayer.worldObj.isRemote) {
            entityPlayer.worldObj.updateAllPlayersSleepingFlag();
        }

        event.setResult(EntityPlayer.SleepResult.OK);
    }
}
