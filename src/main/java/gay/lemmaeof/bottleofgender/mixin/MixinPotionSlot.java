package gay.lemmaeof.bottleofgender.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(targets={"net.minecraft.world.inventory.BrewingStandMenu$PotionSlot"})
public class MixinPotionSlot {
	@Inject(method = "mayPlaceItem", at = @At("HEAD"), cancellable = true)
	private static void injectDragonBreath(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		if (stack.is(Items.DRAGON_BREATH)) info.setReturnValue(true);
	}
}
