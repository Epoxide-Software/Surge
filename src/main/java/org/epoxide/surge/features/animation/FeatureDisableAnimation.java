package org.epoxide.surge.features.animation;

import org.epoxide.surge.command.CommandSurgeWrapper;
import org.epoxide.surge.features.Feature;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Allows for animations to be disabled. This will usually improve performance, especially when
 * in areas with lots of animation like an ocean or the nether.
 */
@SideOnly(Side.CLIENT)
public class FeatureDisableAnimation extends Feature {

    /**
     * Whether or not animations should be displayed. Can be toggled via command.
     */
    private static boolean disableAnimations = false;

    @Override
    public void onInit () {

        if (!FMLClientHandler.instance().hasOptifine())
            CommandSurgeWrapper.addCommand(new CommandAnimation());
    }

    /**
     * Toggles the state of {@link #disableAnimations}. If it was false, it will become true.
     * The reverse is also true.
     */
    public static void toggleAnimation () {

        disableAnimations = !disableAnimations;
    }

    /**
     * Hook for checking if animations should be disabled. If this returns true, animated
     * textures will stay at their first frame. <p> WARNING: This method is referenced directly
     * through ASM. Take care when editing it.
     *
     * @return Whether or not animations should play.
     */
    public static boolean animationDisabled () {

        return disableAnimations;
    }

    @Override
    public void readNBT (NBTTagCompound nbt) {

        disableAnimations = nbt.getBoolean("animationDisabled");
    }

    @Override
    public void writeNBT (NBTTagCompound nbt) {

        nbt.setBoolean("animationDisabled", disableAnimations);
    }
}