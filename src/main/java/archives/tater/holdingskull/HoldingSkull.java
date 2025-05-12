package archives.tater.holdingskull;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

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
					.trackingTickInterval(20)
					.build()
	);

	public static final RegistryKey<Registry<GraveComponentType<?>>> GRAVE_COMPONENT_TYPES_KEY = RegistryKey.ofRegistry(id("grave_component_types"));

	public static final Registry<GraveComponentType<?>> GRAVE_COMPONENT_TYPES = FabricRegistryBuilder.createSimple(GRAVE_COMPONENT_TYPES_KEY)
			.attribute(RegistryAttribute.MODDED)
			.buildAndRegister();

	public static <T extends GraveComponent> GraveComponentType<T> register(String path, Codec<T> codec, Supplier<T> factory) {
		return Registry.register(GRAVE_COMPONENT_TYPES, id(path), new GraveComponentType<>(codec, factory));
	}

	public static final GraveComponentType<VanillaGraveComponent> VANILLA_INVENTORY = register("vanilla", VanillaGraveComponent.CODEC, VanillaGraveComponent::empty);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (entity instanceof PlayerEntity player) {
				entity.getWorld().spawnEntity(new SkullGraveEntity(entity.getWorld(), player));
			}
		});
	}
}