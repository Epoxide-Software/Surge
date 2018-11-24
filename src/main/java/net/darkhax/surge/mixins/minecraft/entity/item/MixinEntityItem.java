package net.darkhax.surge.mixins.minecraft.entity.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

@Mixin(EntityItem.class)
public class MixinEntityItem {
    
    @Inject(method = "searchForOtherItemsNearby()V", at = @At("HEAD"), cancellable = true)
    private void searchForOtherItemsNearby (CallbackInfo info) {

        final ItemStack stack = this.getItem();
        
        if (stack.getCount() >= stack.getMaxStackSize()) {
            
            info.cancel();
        }
    }
    
    @Inject(method = "combineItems(Lnet/minecraft/entity/item/EntityItem;)Z", at = @At("HEAD"), cancellable = true)
    private void combineItems(EntityItem other, CallbackInfoReturnable<Boolean> info) {
        
        final ItemStack stack = this.getItem();
        
        if (stack.getCount() >= stack.getMaxStackSize()) {
            
            info.setReturnValue(false);
        }
    }
    
    @Shadow
    public ItemStack getItem() {
        
        return null;
    }
}
