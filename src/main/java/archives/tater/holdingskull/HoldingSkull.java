package archives.tater.holdingskull;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;

public class HoldingSkull implements ModInitializer {
    public static final String MOD_ID = "holdingskull";
    public static final HoldingSkullConfig CONFIG = WrappedConfig.createToml(FabricLoader.getInstance().getConfigDir(), "", MOD_ID, HoldingSkullConfig.class);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static class HoldingSkullConfig extends WrappedConfig {
        @Comment("The amounts of ticks that needs to pass for an empty skull grave to despawn.")
        public int maxEmptyTicks = 20 * 60;
        @Comment("The amounts of ticks that needs to pass for an empty skull grave to become public (unclaimed).")
        @Comment("Set to 0 to make them public by default.")
        public int maxUnclaimedTicks = 20 * 15 * 60;
        @Comment("Whether the skull grave should decay into skeleton shape when it is unclaimed.")
        @Comment("Recommended to set it to false if maxUnclaimedTicks is 0.")
        public boolean shouldDecayToSkeleton = true;
        @Comment("Whether ops should be able to bypass the private skulls (claimed).")
        public boolean opsCanBypassUnclaimedSkulls = true;
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

    public static final MenuType<SkullGraveMenu> SKULL_GRAVE_MENU = Registry.register(BuiltInRegistries.MENU, id("skull_grave"), new ExtendedMenuType<>(SkullGraveMenu::new, SkullGraveMenu.SkullGraveData.STREAM_CODEC));

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
    }
}