package gay.lemmaeof.bottleofgender.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gay.lemmaeof.bottleofgender.BottleOfGender;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;

public class GenderManager extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final GenderManager INSTANCE = new GenderManager();
	private final Map<ResourceLocation, Gender> genders = new HashMap<>();
	private final Gender NULL = new Gender(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	private final Random RANDOM = new Random();

	public GenderManager() {
		super(GSON, "genders");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		genders.clear();
		genders.put(new ResourceLocation(BottleOfGender.MODID, "null"), NULL);
		for (ResourceLocation id : jsons.keySet()) {
			JsonObject json = GsonHelper.convertToJsonObject(jsons.get(id), id.toString());
			Optional<UUID> uuid = Optional.empty();
			Optional<Component> term = Optional.empty();
			Optional<TextColor> color = Optional.empty();
			Optional<Gender.GenderProperties> properties = Optional.empty();
			//this could be a codec but I don't want to have to deal with string to uuid today thank you very MUCH
			String uuidRaw = GsonHelper.getAsString(json, "uuid", null);
			if (uuidRaw != null) {
				uuid = Optional.of(UUID.fromString(uuidRaw));
			}
			JsonObject termRaw = GsonHelper.getAsJsonObject(json, "term", null);
			if (termRaw != null) {
				term = Optional.ofNullable(Component.Serializer.fromJson(termRaw));
			}
			String colorRaw = GsonHelper.getAsString(json, "color", null);
			if (colorRaw != null) {
				color = Optional.ofNullable(TextColor.parseColor(colorRaw));
			}
			JsonObject propertiesRaw = GsonHelper.getAsJsonObject(json, "properties", null);
			if (propertiesRaw != null) {
				Optional<Float> scale = Optional.empty();
				if (propertiesRaw.has("scale")) {
					scale = Optional.of(GsonHelper.getAsFloat(propertiesRaw, "scale"));
				}
				properties = Optional.of(new Gender.GenderProperties(scale));
			}
			genders.put(id, new Gender(uuid, term, color, properties));
		}
	}

	public Gender getGender(ResourceLocation id) {
		return genders.getOrDefault(id, NULL);
	}

	public Set<ResourceLocation> getGenderIds() {
		return genders.keySet();
	}

	public Gender getDefault() {
		return NULL;
	}

	public Gender getRandomGender() {
		Gender[] genderArray = genders.values().toArray(new Gender[0]);
		return genderArray[RANDOM.nextInt(genderArray.length)];
	}

	@Override
	public String getName() {
		return "Gender";
	}

	@Override
	public ResourceLocation getFabricId() {
		return new ResourceLocation(BottleOfGender.MODID, "gender");
	}
}
