package archives.tater.holdingskull;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

@SuppressWarnings("resource")
public class SkullGraveEntity extends Entity implements MenuProvider, TraceableEntity {
    protected static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(
            SkullGraveEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE
    );
    private final SimpleContainer inventory = new SimpleContainer(127) {
        @Override
        public boolean canAddItem(ItemStack stack) {
            return false;
        }
    };
    private int ticksEmpty = 0;

    public static final int MAX_EMPTY_TICKS = 20 * 60;

    public SkullGraveEntity(EntityType<? extends SkullGraveEntity> type, Level world) {
        super(type, world);
    }

    public SkullGraveEntity(Level world, Player owner) {
        super(HoldingSkull.SKULL_GRAVE, world);
        setOwner(owner);
        setPos(owner.position());
    }

    public void setOwner(@Nullable Player owner) {
        inventory.items.clear();
        if (owner == null) {
            this.entityData.set(DATA_OWNERUUID_ID, Optional.empty());
            setCustomName(null);
            return;
        }
        this.entityData.set(DATA_OWNERUUID_ID, Optional.of(owner).map(EntityReference::of));
        var playerInventory = owner.getInventory();
        for (int i = 0; i < playerInventory.getContainerSize(); i++) {
            inventory.items.set(i, playerInventory.getItem(i));
            playerInventory.setItem(i, ItemStack.EMPTY);
        }
        // TODO plugins
        setCustomName(owner.getName());
    }

    @Nullable
    public EntityReference<LivingEntity> getOwnerReference() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    @Nullable
    @Override
    public Player getOwner() {
        return this.entityData.get(DATA_OWNERUUID_ID).map(EntityReference::getUUID).map(uuid -> level().getPlayerByUUID(uuid)).orElse(null);
    }

    public boolean isOwner(Player player) {
        return player.getUUID().equals(this.entityData.get(DATA_OWNERUUID_ID).map(EntityReference::getUUID).orElse(null));
    }

    @Override
    public void tick() {
        if (getY() < level().getMinY()) {
            setPos(getX(), level().getMaxY(), getZ());
            setNoGravity(true);
        }
        super.tick();
        handlePortal();
        if (isInWater() && getFluidHeight(FluidTags.WATER) > 0.1F) {
            applyWaterBuoyancy();
        } else if (isInLava() && getFluidHeight(FluidTags.LAVA) > 0.1F) {
            applyLavaBuoyancy();
        } else {
            applyGravity();
        }
        move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(0.98));
        if (onGround())
            setDeltaMovement(getDeltaMovement().multiply(0.7, -0.5, 0.7));

        updateFluidInteraction();

        if (inventory.isEmpty()) {
            ticksEmpty++;
            if (ticksEmpty > MAX_EMPTY_TICKS)
                discard();
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (!isOwner(player)) return;

        player.makeSound(SoundEvents.ITEM_PICKUP);

        if (level().isClientSide()) return;

        player.take(this, 1);

        var remainingStacks = new ArrayList<ItemStack>();
        var playerInventory = player.getInventory();
        for (var i = 0; i < inventory.getContainerSize(); i++) {
            var stack = inventory.getItem(i);

            if (i >= playerInventory.getContainerSize()) {
                remainingStacks.add(stack);
                continue;
            }

            if (!stack.isEmpty()) {
                var replacedStack = playerInventory.getItem(i);
                playerInventory.setItem(i, stack);
                if (!replacedStack.isEmpty())
                    remainingStacks.add(replacedStack);
            }
        }
        for (var stack : remainingStacks) {
            if (!player.addItem(stack) && level() instanceof ServerLevel serverLevel)
                player.spawnAtLocation(serverLevel, stack);
        }

        discard();
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    private void applyWaterBuoyancy() {
        Vec3 vec3d = this.getDeltaMovement();
        this.setDeltaMovement(vec3d.x * 0.99F, vec3d.y + (vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.99F);
    }

    private void applyLavaBuoyancy() {
        Vec3 vec3d = this.getDeltaMovement();
        this.setDeltaMovement(vec3d.x * 0.95F, vec3d.y + (vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.95F);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (!player.isSecondaryUseActive()) {
            return super.interact(player, hand, location);
        }

        if ((player instanceof ServerPlayer serverPlayer))
            serverPlayer.openMenu(this);

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean shouldShowName() {
        return super.shouldShowName() || hasCustomName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new ChestMenu(MenuType.GENERIC_9x5, syncId, playerInventory, inventory, 5);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    private static final String TICKS_EMPTY_NBT = "TicksEmpty";

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        EntityReference<LivingEntity> owner = EntityReference.readWithOldOwnerConversion(input, "Owner", this.level());
        if (owner != null) {
            try {
                this.entityData.set(DATA_OWNERUUID_ID, Optional.of(owner));
            } catch (Throwable _) {
            }
        } else {
            this.entityData.set(DATA_OWNERUUID_ID, Optional.empty());
        }

        ticksEmpty = input.getInt(TICKS_EMPTY_NBT).orElseThrow();

        ContainerHelper.loadAllItems(input, inventory.items);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        EntityReference<LivingEntity> owner = this.getOwnerReference();
        EntityReference.store(owner, output, "Owner");
        output.putInt(TICKS_EMPTY_NBT, ticksEmpty);
        ContainerHelper.saveAllItems(output, inventory.items);
    }
}
