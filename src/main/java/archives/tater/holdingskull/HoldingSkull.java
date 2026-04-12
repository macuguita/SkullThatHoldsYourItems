package archives.tater.holdingskull;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class HoldingSkull implements ModInitializer {
    public static final String MOD_ID = "holdingskull";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static final EntityType<SkullGraveEntity> SKULL_GRAVE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            id("skull_grave"),
            EntityType.Builder.<SkullGraveEntity>of(SkullGraveEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.2125F)
                    .clientTrackingRange(6)
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), id("skull_grave")))
    );

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
    }
}