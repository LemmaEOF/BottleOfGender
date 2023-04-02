package gay.lemmaeof.bottleofgender;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gay.lemmaeof.bottleofgender.data.Gender;
import gay.lemmaeof.bottleofgender.data.GenderComponent;
import gay.lemmaeof.bottleofgender.data.GenderManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class BottleOfGenderItem extends Item {
	private static final Random RANDOM = new Random();
	public BottleOfGenderItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		livingEntity.gameEvent(GameEvent.DRINK);
		if (!level.isClientSide) {
			BottleOfGenderItem.changeGender(stack, level, livingEntity);
			return livingEntity instanceof Player p && p.isCreative()? stack : Items.GLASS_BOTTLE.getDefaultInstance();
		}
		return stack;
	}

	public static void changeGender(ItemStack stack, Level level, LivingEntity livingEntity) {
		if (livingEntity instanceof Player player) {
			GenderComponent gender = BottleOfGender.GENDER_COMPONENT.get(player);
			CompoundTag tag = stack.getTag();
			if (tag != null && tag.contains("Gender", Tag.TAG_STRING)) {
				ResourceLocation id = new ResourceLocation(tag.getString("Gender"));
				gender.setGender(GenderManager.INSTANCE.getGender(id));
			} else {
				Gender setTo = GenderManager.INSTANCE.getRandomGender();
				if (setTo.equals(gender.getGender())) setTo = GenderManager.INSTANCE.getRandomGender();
				gender.setGender(setTo);
			}
		}
	}

	@Override
	public int getUseDuration(ItemStack itemStack) {
		return 32;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemStack) {
		return UseAnim.DRINK;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		return ItemUtils.startUsingInstantly(level, player, interactionHand);
	}
}
