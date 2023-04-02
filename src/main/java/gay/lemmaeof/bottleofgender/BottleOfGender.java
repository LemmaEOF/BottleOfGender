package gay.lemmaeof.bottleofgender;


import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import gay.lemmaeof.bottleofgender.data.GenderComponent;
import gay.lemmaeof.bottleofgender.data.GenderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

public class BottleOfGender implements ModInitializer, EntityComponentInitializer {
	public static final String MODID = "bottleofgender";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final ComponentKey<GenderComponent> GENDER_COMPONENT = ComponentRegistry.getOrCreate(new ResourceLocation(MODID, "gender"), GenderComponent.class);
	public static final Item BOTTLE_OF_GENDER = Registry.register(
			BuiltInRegistries.ITEM,
			new ResourceLocation(MODID, "bottle_of_gender"),
			new BottleOfGenderItem(new FabricItemSettings().maxCount(1))
	);

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(GenderManager.INSTANCE);
	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(GENDER_COMPONENT, GenderComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}
}
