package net.darkhax.surge.mixins.minecraft.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.darkhax.surge.core.SurgeConfiguration;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants.NBT;

@Mixin(Entity.class)
public class MixinEntity {

    @Shadow
    public AxisAlignedBB getEntityBoundingBox () {

        return null;
    }

    @Shadow
    public void setEntityBoundingBox (AxisAlignedBB bb) {

    }

    @Shadow
    protected NBTTagList newDoubleNBTList (double... numbers) {

        return null;
    }

    @Inject(method = "writeToNBT(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;", at = @At("HEAD"))
    private void writeToNBT (NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> callback) {

        if (SurgeConfiguration.fixWallGlitch) {

            final AxisAlignedBB bounds = this.getEntityBoundingBox();

            if (bounds != null) {

                tag.setTag("SurgeAABB", this.newDoubleNBTList(bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ));
            }
        }
    }

    @Inject(method = "readFromNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("RETURN"))
    private void readFromNBT (NBTTagCompound tag, CallbackInfo callback) {

        if (SurgeConfiguration.fixWallGlitch && tag.hasKey("SurgeAABB")) {

            final NBTTagList verticies = tag.getTagList("SurgeAABB", NBT.TAG_DOUBLE);
            this.setEntityBoundingBox(new AxisAlignedBB(verticies.getDoubleAt(0), verticies.getDoubleAt(1), verticies.getDoubleAt(2), verticies.getDoubleAt(3), verticies.getDoubleAt(4), verticies.getDoubleAt(5)));
        }
    }
}