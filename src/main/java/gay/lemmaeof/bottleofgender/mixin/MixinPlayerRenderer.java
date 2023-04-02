package gay.lemmaeof.bottleofgender.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import gay.lemmaeof.bottleofgender.BottleOfGender;
import gay.lemmaeof.bottleofgender.data.GenderComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {
	@ModifyArg(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
	at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/entity/LivingEntityRenderer.renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"),
	index = 1)
	private Component injectGender(Entity e, Component original, PoseStack poseStack, MultiBufferSource source, int i) {
		GenderComponent gender = BottleOfGender.GENDER_COMPONENT.get(e);
		if (gender.getGender().term().isPresent()) {
			MutableComponent comp = Component.empty().append(original).append(" (").append(gender.getGender().term().get()).append(")");
			if (gender.getGender().color().isPresent()) {
				comp.setStyle(Style.EMPTY.withColor(gender.getGender().color().get()));
			}
			return comp;
		} else {
			return original;
		}
	}
}
