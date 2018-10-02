package net.darkhax.surge.mixins.minecraft.client.audio;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.darkhax.surge.core.SurgeConfiguration;
import net.darkhax.surge.core.SurgeLoadingPlugin;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;

@Mixin(SoundHandler.class)
public class MixinSoundHandler {

    @Shadow
    private static final Logger LOGGER = LogManager.getLogger();

    @Shadow
    private final SoundRegistry soundRegistry = new SoundRegistry();

    @Shadow
    private SoundManager sndManager;

    @Shadow
    private void loadSoundResource (ResourceLocation location, SoundList sounds) {

    }

    @Shadow
    protected Map<String, SoundList> getSoundMap (InputStream stream) {

        return null;
    }

    @Inject(method = "onResourceManagerReload(Lnet/minecraft/client/resources/IResourceManager;)V", at = @At("HEAD"), cancellable = true)
    private void onResourceManagerReload (IResourceManager resourceManager, CallbackInfo info) {

        this.soundRegistry.clearMap();

        final List<Tuple<ResourceLocation, SoundList>> soundLists = new LinkedList<>();
        this.loadSoundLists(resourceManager, soundLists);
        this.loadSounds(soundLists);

        if (!SurgeConfiguration.disableDebugSoundInfo) {

            this.debugSounds();
        }

        this.sndManager.reloadSoundSystem();

        info.cancel();
    }

    private void loadSounds (List<Tuple<ResourceLocation, SoundList>> soundLists) {

        final ProgressBar resourcesBar = ProgressManager.push("Loading sounds", soundLists.size());

        for (final Tuple<ResourceLocation, SoundList> soundList : soundLists) {

            resourcesBar.step(soundList.getFirst().toString());

            try {

                this.loadSoundResource(soundList.getFirst(), soundList.getSecond());
            }

            catch (final RuntimeException e) {

                // TODO better handling here
                LOGGER.warn("Invalid sounds.json", e);
            }
        }

        ProgressManager.pop(resourcesBar);
    }

    private void loadSoundLists (IResourceManager resourceManager, List<Tuple<ResourceLocation, SoundList>> soundLists) {

        for (final String s : resourceManager.getResourceDomains()) {

            try {

                for (final IResource managerSoundList : resourceManager.getAllResources(new ResourceLocation(s, "sounds.json"))) {

                    for (final Entry<String, SoundList> entry : this.getSoundMap(managerSoundList.getInputStream()).entrySet()) {

                        soundLists.add(new Tuple<>(new ResourceLocation(s, entry.getKey()), entry.getValue()));
                    }
                }
            }

            catch (final Exception e) {

                SurgeLoadingPlugin.LOG.error("Unable to load sounds.json for {}.", s);
                e.printStackTrace();
            }
        }
    }

    private void debugSounds () {

        for (final ResourceLocation resourcelocation : this.soundRegistry.getKeys()) {

            if (SoundEvent.REGISTRY.getObject(resourcelocation) == null) {

                LOGGER.debug("Missing sound event: {}", resourcelocation);
            }

            else {

                final SoundEventAccessor accessor = this.soundRegistry.getObject(resourcelocation);

                if (accessor.getSubtitle() instanceof TextComponentTranslation) {
                    final String s1 = ((TextComponentTranslation) accessor.getSubtitle()).getKey();

                    if (!I18n.hasKey(s1)) {
                        LOGGER.debug("Missing subtitle {} for event: {}", s1, resourcelocation);
                    }
                }
            }
        }
    }
}