package gay.lemmaeof.bottleofgender.mixin;

import java.util.List;

import gay.lemmaeof.bottleofgender.BottleOfGender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

@Mixin(PotionBrewing.class)
public abstract class MixinPotionBrewing {

	@Shadow @Final private static List<Ingredient> ALLOWED_CONTAINERS;

	@Inject(method = "hasPotionMix", at = @At("HEAD"), cancellable = true)
	private static void injectCanBrewGender(ItemStack potion, ItemStack ingredient, CallbackInfoReturnable<Boolean> info) {
		if (potion.is(Items.DRAGON_BREATH) && ingredient.is(Items.LIGHT_BLUE_DYE)) info.setReturnValue(true);
	}

	@Inject(method = "mix", at = @At("HEAD"), cancellable = true)
	private static void injectGenderBrewing(ItemStack ingredient, ItemStack potion, CallbackInfoReturnable<ItemStack> info) {
		if (potion.is(Items.DRAGON_BREATH) && ingredient.is(Items.LIGHT_BLUE_DYE)) info.setReturnValue(new ItemStack(BottleOfGender.BOTTLE_OF_GENDER));
	}

	@Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
	private static void injectGenderIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		if (stack.is(Items.LIGHT_BLUE_DYE)) info.setReturnValue(true);
	}

	@Inject(method = "bootStrap", at = @At("TAIL"))
	private static void injectGenderContainer(CallbackInfo info) {
		ALLOWED_CONTAINERS.add(Ingredient.of(Items.DRAGON_BREATH));
	}
}
