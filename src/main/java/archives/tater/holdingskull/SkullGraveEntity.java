package archives.tater.holdingskull;

import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkullGraveEntity extends Entity implements NamedScreenHandlerFactory, Ownable, GraveComponentHolder {
    private @Nullable PlayerEntity owner = null;
    private @Nullable UUID ownerUuid = null;
    private Map<GraveComponentType<?>, GraveComponent> ownerInventoryComponents = new HashMap<>();

    public SkullGraveEntity(EntityType<? extends SkullGraveEntity> type, World world) {
        super(type, world);
    }

    public SkullGraveEntity(World world, PlayerEntity owner) {
        super(HoldingSkull.SKULL_GRAVE, world);
        setOwner(owner);
        setPosition(owner.getPos());
    }

    public void setOwner(@Nullable PlayerEntity owner) {
        if (owner == null) {
            this.owner = null;
            ownerUuid = null;
            ownerInventoryComponents = null;
            setCustomName(null);
            return;
        }
        this.owner = owner;
        ownerUuid = owner.getUuid();
        ownerInventoryComponents.put(HoldingSkull.VANILLA_INVENTORY, VanillaGraveComponent.of(owner.getInventory()));
        // TODO plugins
        setCustomName(owner.getName());
    }

    @Nullable
    @Override
    public PlayerEntity getOwner() {
        if (owner != null && !owner.isRemoved()) {
            return owner;
        }

        if (ownerUuid == null) return null;

        owner = getWorld().getPlayerByUuid(this.ownerUuid);
        return owner;
    }

    public boolean isOwner(PlayerEntity player) {
        return player.getUuid().equals(ownerUuid);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends GraveComponent> @Nullable T get(GraveComponentType<T> type) {
        return (T) ownerInventoryComponents.get(type);
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T extends GraveComponent> T set(GraveComponentType<T> type, T value) {
        return (T) ownerInventoryComponents.put(type, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends GraveComponent> @Nullable T remove(GraveComponentType<T> type) {
        return (T) ownerInventoryComponents.remove(type);
    }

    @Override
    public <T extends GraveComponent> boolean contains(GraveComponentType<T> type) {
        return ownerInventoryComponents.containsKey(type);
    }

    @Override
    public void tick() {
        super.tick();
        tickPortalTeleportation();
        applyGravity();
        move(MovementType.SELF, getVelocity());
        setVelocity(getVelocity().multiply(0.98));
        if (isOnGround())
            setVelocity(getVelocity().multiply(0.7, -0.5, 0.7));

        updateWaterState();
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!isOwner(player)) {
            super.onPlayerCollision(player);
            return;
        }
        player.sendPickup(this, 1);
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!player.shouldCancelInteraction()) {
            return super.interact(player, hand);
        }

        if ((player instanceof ServerPlayerEntity serverPlayer))
            serverPlayer.openHandledScreen(this);

        return ActionResult.SUCCESS;
    }

    @Override
    public boolean shouldRenderName() {
        return super.shouldRenderName() || hasCustomName();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        if (ownerInventoryComponents == null)
            return null;
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X5, syncId, playerInventory, new CompositeInventory(ownerInventoryComponents.values().stream().map(GraveComponent::asInventory).toArray(Inventory[]::new)), 5);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    private static final String INVENTORY_NBT = "Inventory";
    private static final String OWNER_NBT = "Owner";
    private static final Codec<Map<GraveComponentType<?>, GraveComponent>> COMPONENTS_CODEC = Codec.dispatchedMap(HoldingSkull.GRAVE_COMPONENT_TYPES.getCodec(), GraveComponentType::codec);

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains(OWNER_NBT))
            ownerUuid = nbt.getUuid(OWNER_NBT);
        else
            ownerUuid = null;
        owner = null;

        getOwner();

        if (nbt.contains(INVENTORY_NBT)) {
            ownerInventoryComponents.clear();
            COMPONENTS_CODEC.decode(RegistryOps.of(NbtOps.INSTANCE, getRegistryManager()), nbt.getCompound(INVENTORY_NBT)).ifSuccess(pair ->
                    ownerInventoryComponents.putAll(pair.getFirst())
            );
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (ownerInventoryComponents != null) {
            COMPONENTS_CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, getRegistryManager()), ownerInventoryComponents).ifSuccess(data ->
                    nbt.put(INVENTORY_NBT, data)
            );
        }
        if (ownerUuid != null)
            nbt.putUuid(OWNER_NBT, ownerUuid);
    }
}
