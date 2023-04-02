package gay.lemmaeof.bottleofgender.data;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public record Gender(Optional<UUID> skinUuid, Optional<Component> term, Optional<TextColor> color, Optional<GenderProperties> properties) {

	public record GenderProperties(Optional<Float> scale) {} //TODO: more gender properties later
}
