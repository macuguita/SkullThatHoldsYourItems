package archives.tater.holdingskull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class SkullGraveEntity extends Entity implements NamedScreenHandlerFactory, Ownable {
    private @Nullable PlayerEntity owner = null;
    private @Nullable UUID ownerUuid = null;
    private final SimpleInventory inventory = new SimpleInventory(127) {
        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }
    };
    private int ticksEmpty = 0;

    public static final int MAX_EMPTY_TICKS = 20 * 60;

    public SkullGraveEntity(EntityType<? extends SkullGraveEntity> type, World world) {
        super(type, world);
    }

    public SkullGraveEntity(World world, PlayerEntity owner) {
        super(HoldingSkull.SKULL_GRAVE, world);
        setOwner(owner);
        setPosition(owner.getPos());
    }

    public void setOwner(@Nullable PlayerEntity owner) {
        inventory.heldStacks.clear();
        if (owner == null) {
            this.owner = null;
            ownerUuid = null;
            setCustomName(null);
            return;
        }
        this.owner = owner;
        ownerUuid = owner.getUuid();
        var playerInventory = owner.getInventory();
        for (int i = 0; i < playerInventory.size(); i++) {
            inventory.heldStacks.set(i, playerInventory.getStack(i));
            playerInventory.setStack(i, ItemStack.EMPTY);
        }
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

    @Override
    public void tick() {
        if (getY() < getWorld().getBottomY()) {
            setPosition(getX(), getWorld().getBottomY(), getZ());
            setNoGravity(true);
        }
        super.tick();
        tickPortalTeleportation();
        if (isTouchingWater() && getFluidHeight(FluidTags.WATER) > 0.1F) {
            applyWaterBuoyancy();
        } else if (isInLava() && getFluidHeight(FluidTags.LAVA) > 0.1F) {
            applyLavaBuoyancy();
        } else {
            applyGravity();
        }
        move(MovementType.SELF, getVelocity());
        setVelocity(getVelocity().multiply(0.98));
        if (isOnGround())
            setVelocity(getVelocity().multiply(0.7, -0.5, 0.7));

        updateWaterState();

        if (inventory.isEmpty()) {
            ticksEmpty++;
            if (ticksEmpty > MAX_EMPTY_TICKS)
                discard();
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!isOwner(player)) return;

        player.playSound(SoundEvents.ENTITY_ITEM_PICKUP);

        if (getWorld().isClient) return;

        player.sendPickup(this, 1);

        var remainingStacks = new ArrayList<ItemStack>();
        var playerInventory = player.getInventory();
        for (var i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);

            if (i >= playerInventory.size()) {
                remainingStacks.add(stack);
                continue;
            }

            if (!stack.isEmpty()) {
                var replacedStack = playerInventory.getStack(i);
                playerInventory.setStack(i, stack);
                if (!replacedStack.isEmpty())
                    remainingStacks.add(replacedStack);
            }
        }
        for (var stack : remainingStacks) {
            if (!player.giveItemStack(stack))
                player.dropStack(stack);
        }

        discard();
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    protected double getGravity() {
        return 0.04;
    }

    private void applyWaterBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * 0.99F, vec3d.y + (vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.99F);
    }

    private void applyLavaBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * 0.95F, vec3d.y + (vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.95F);
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
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X5, syncId, playerInventory, inventory, 5);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    private static final String OWNER_NBT = "Owner";
    private static final String TICKS_EMPTY_NBT = "TicksEmpty";

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains(OWNER_NBT))
            ownerUuid = nbt.getUuid(OWNER_NBT);
        else
            ownerUuid = null;
        owner = null;

        getOwner();

        ticksEmpty = nbt.getInt(TICKS_EMPTY_NBT);

        Inventories.readNbt(nbt, inventory.heldStacks, getRegistryManager());
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (ownerUuid != null)
            nbt.putUuid(OWNER_NBT, ownerUuid);
        nbt.putInt(TICKS_EMPTY_NBT, ticksEmpty);
        Inventories.writeNbt(nbt, inventory.heldStacks, getRegistryManager());
    }
}
