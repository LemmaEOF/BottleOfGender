package gay.lemmaeof.bottleofgender.data;

import java.util.Optional;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import gay.lemmaeof.bottleofgender.BottleOfGender;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.transform.EntityTransformType;

public class GenderComponent implements Component, AutoSyncedComponent {
	private final Player player;
	private Gender gender = GenderManager.INSTANCE.getDefault();

	public GenderComponent(Player player) {
		this.player = player;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
		GameProfile profile = player.getGameProfile();
		EntityTransformType tfType = new EntityTransformType(
				Optional.empty(),
				gender.properties().orElse(new Gender.GenderProperties(Optional.of(1f))).scale().orElse(1f),
				Optional.of(new GameProfile(gender.skinUuid().orElse(profile.getId()), profile.getName()))
		);
		player.setTransform(tfType);
		if (gender.term().isPresent()) {
			player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("msg.bottleofgender.newgender", gender.term().get()));
		} else {
			player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("msg.bottleofgender.newgender.null"));
		}
		BottleOfGender.GENDER_COMPONENT.sync(player);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		Optional<UUID> uuid = Optional.empty();
		Optional<net.minecraft.network.chat.Component> term = Optional.empty();
		Optional<TextColor> color = Optional.empty();
		Optional<Gender.GenderProperties> properties = Optional.empty();
		if (tag.hasUUID("uuid")) {
			uuid = Optional.of(tag.getUUID("uuid"));
		}
		if (tag.contains("term", Tag.TAG_STRING)) {
			term = Optional.of(net.minecraft.network.chat.Component.Serializer.fromJson(tag.getString("term")));
		}
		if (tag.contains("color", Tag.TAG_INT)) {
			color = Optional.of(TextColor.fromRgb(tag.getInt("color")));
		}
		if (tag.contains("properties", Tag.TAG_COMPOUND)) {
			CompoundTag propsRaw = tag.getCompound("properties");
			Optional<Float> scale = Optional.empty();
			if (propsRaw.contains("scale", Tag.TAG_FLOAT)) {
				scale = Optional.of(propsRaw.getFloat("scale"));
			}
			properties = Optional.of(new Gender.GenderProperties(scale));
		}
		gender = new Gender(uuid, term, color, properties);
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		if (gender.skinUuid().isPresent()) {
			tag.putUUID("uuid", gender.skinUuid().get());
		}
		if (gender.term().isPresent()) {
			tag.putString("term", net.minecraft.network.chat.Component.Serializer.toJson(gender.term().get()));
		}
		if (gender.color().isPresent()) {
			tag.putInt("color", gender.color().get().getValue());
		}
		if (gender.properties().isPresent()) {
			Gender.GenderProperties properties = gender.properties().get();
			CompoundTag propsTag = new CompoundTag();
			if (properties.scale().isPresent()) {
				propsTag.putFloat("scale", properties.scale().get());
			}
			tag.put("properties", propsTag);
		}
	}

	//have to do this because `writeNbt` changed from taking a `CompoundTag` to a `Tag` so it needs a recompile, wheeeeeeeeeeee
	@Override
	public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
		CompoundTag tag = new CompoundTag();
		this.writeToNbt(tag);
		buf.writeNbt(tag);
	}
}
