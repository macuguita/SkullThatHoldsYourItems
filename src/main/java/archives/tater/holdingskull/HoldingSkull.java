package archives.tater.holdingskull;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HoldingSkull implements ModInitializer {
	public static final String MOD_ID = "holdingskull";

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityType<SkullGraveEntity> SKULL_GRAVE = Registry.register(
			Registries.ENTITY_TYPE,
			id("skull_grave"),
			EntityType.Builder.<SkullGraveEntity>create(SkullGraveEntity::new, SpawnGroup.MISC)
					.makeFireImmune()
					.dimensions(0.5F, 0.5F)
					.eyeHeight(0.2125F)
					.maxTrackingRange(6)
					.build()
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
	}
}