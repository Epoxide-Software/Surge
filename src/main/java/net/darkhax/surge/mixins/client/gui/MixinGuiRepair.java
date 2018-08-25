package net.darkhax.surge.mixins.client.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiTextField;

@Mixin(GuiRepair.class)
public class MixinGuiRepair {

    @Shadow()
    private GuiTextField nameField;

    @Inject(method = "initGui", at = @At("RETURN"), cancellable = true)
    private void initGui (CallbackInfo ci) {

        this.nameField.setMaxStringLength(256);
    }

}
