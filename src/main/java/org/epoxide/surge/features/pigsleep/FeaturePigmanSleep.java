package org.epoxide.surge.features.pigsleep;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.epoxide.surge.asm.ASMUtils;
import org.epoxide.surge.features.Feature;

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

        final EntityPlayer entityPlayer = event.getEntityPlayer();

        if (!entityPlayer.worldObj.isRemote) {

            if (entityPlayer.isPlayerSleeping() || !entityPlayer.isEntityAlive()) {

                event.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
                return;
            }

            else if (!entityPlayer.worldObj.provider.isSurfaceWorld()) {

                event.setResult(EntityPlayer.SleepResult.NOT_POSSIBLE_HERE);
                return;
            }

            else if (entityPlayer.worldObj.isDaytime()) {

                event.setResult(EntityPlayer.SleepResult.NOT_POSSIBLE_NOW);
                return;
            }

            else if (Math.abs(entityPlayer.posX - event.getPos().getX()) > 3.0D || Math.abs(entityPlayer.posY - event.getPos().getY()) > 2.0D || Math.abs(entityPlayer.posZ - event.getPos().getZ()) > 3.0D) {

                event.setResult(EntityPlayer.SleepResult.TOO_FAR_AWAY);
                return;
            }

            final List<EntityMob> list = entityPlayer.worldObj.<EntityMob>getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(event.getPos().getX() - 8.0D, event.getPos().getY() - 5.0D, event.getPos().getZ() - 8.0D, event.getPos().getX() + 8.0D, event.getPos().getY() + 5.0D, event.getPos().getZ() + 8.0D));
            final List<EntityMob> mobList = new ArrayList<>();

            for (final EntityMob mob : list) {

                if (mob instanceof EntityPigZombie)
                    if (((EntityPigZombie) mob).angerTargetUUID != entityPlayer.getUniqueID())
                        continue;

                mobList.add(mob);
            }

            if (!mobList.isEmpty()) {

                event.setResult(EntityPlayer.SleepResult.NOT_SAFE);
                return;
            }
        }

        if (entityPlayer.isRiding())
            entityPlayer.dismountRidingEntity();

        try {

            final Method m = Entity.class.getDeclaredMethod(ASMUtils.isSrg ? "func_70105_a" : "setSize", float.class, float.class);
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

            final EnumFacing enumfacing = state.getBlock().getBedDirection(state, entityPlayer.worldObj, event.getPos());
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
                default:
                    break;
            }

            entityPlayer.setRenderOffsetForSleep(enumfacing);
            entityPlayer.setPosition(event.getPos().getX() + f, event.getPos().getY() + 0.6875F, event.getPos().getZ() + f1);
        }

        else
            entityPlayer.setPosition(event.getPos().getX() + 0.5F, event.getPos().getY() + 0.6875F, event.getPos().getZ() + 0.5F);

        entityPlayer.sleeping = true;
        entityPlayer.sleepTimer = 0;
        entityPlayer.bedLocation = event.getPos();
        entityPlayer.motionX = 0.0D;
        entityPlayer.motionY = 0.0D;
        entityPlayer.motionZ = 0.0D;

        if (!entityPlayer.worldObj.isRemote)
            entityPlayer.worldObj.updateAllPlayersSleepingFlag();

        event.setResult(EntityPlayer.SleepResult.OK);
    }
}
