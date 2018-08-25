package net.darkhax.surge.mixins.client.gui;

import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRepair.class)
public class MixinGuiRepair {
    
    @Shadow()
    private GuiTextField nameField;
    
    @Inject(method = "initGui", at = @At("RETURN"), cancellable = true)
    private void initGui(CallbackInfo ci) {
        nameField.setMaxStringLength(256);
    }
    
}
