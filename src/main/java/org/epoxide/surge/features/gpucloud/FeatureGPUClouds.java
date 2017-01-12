package org.epoxide.surge.features.gpucloud;

import org.epoxide.surge.command.CommandSurgeWrapper;
import org.epoxide.surge.features.Feature;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Replaces vanilla cloud rendering with one that uses the GPU for cloud geometry. This causes
 * significant improvements in cloud performance.
 */
@SideOnly(Side.CLIENT)
public class FeatureGPUClouds extends Feature {

    /**
     * Whether or not the new cloud renderer should be used. Can be toggled via command.
     */
    private static boolean renderClouds = true;

    /**
     * Instance of the GPU cloud renderer.
     */
    private static CloudRenderer INSTANCE;

    @Override
    public void onInit () {

        CommandSurgeWrapper.addCommand(new CommandClouds());
    }

    /**
     * Toggles the state of {@link #renderClouds}. If it was true, it will become false. The
     * opposite is also true.
     */
    public static void toggleRenderClouds () {

        renderClouds = !renderClouds;
    }

    /**
     * A hook to allow cloud rendering to be replaced with the GPU geometry clouds.
     * <p>
     * WARNING: This method is referenced directly through ASM. Take care when editing it.
     *
     * @return Whether or not the custom cloud renderer should be used.
     */
    public static boolean shouldRenderClouds () {

        return renderClouds;
    }

    /**
     * Gets the instance of the GPU cloud renderer.
     *
     * @return The effectively final instance of the gpu cloud renderer.
     */
    public static CloudRenderer getInstance () {

        if (INSTANCE == null)
            INSTANCE = new CloudRenderer();

        return INSTANCE;
    }

    @Override
    public void readNBT (NBTTagCompound nbt) {

        renderClouds = nbt.getBoolean("renderClouds");
    }

    @Override
    public void writeNBT (NBTTagCompound nbt) {

        nbt.setBoolean("renderClouds", renderClouds);
    }
}