package net.darkhax.surge.core;

import java.util.Map;

import javax.annotation.Nullable;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions("net.darkhax.surge.core")
public class SurgeLoadingPlugin implements IFMLLoadingPlugin {

    public static long firstLaunchTime = 0;

    public SurgeLoadingPlugin () {

        firstLaunchTime = System.currentTimeMillis();
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.surge.json");
    }

    @Override
    public String[] getASMTransformerClass () {

        return new String[0];
    }

    @Override
    public String getModContainerClass () {

        return null;
    }

    @Nullable
    @Override
    public String getSetupClass () {

        return null;
    }

    @Override
    public void injectData (Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass () {

        return null;
    }
}