package archives.tater.holdingskull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class CompositeInventory implements Inventory {
    private final Inventory[] inventories;

    public CompositeInventory(Inventory... inventories) {
        this.inventories = inventories;
    }

    @Override
    public int size() {
        var size = 0;
        for (var inventory : inventories)
            size += inventory.size();
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (var inventory : inventories)
            if (!inventory.isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < 0) return ItemStack.EMPTY;
        var slotsLeft = slot;
        for (var inventory : inventories) {
            var size = inventory.size();
            if (slotsLeft < size)
                return inventory.getStack(slotsLeft);
            slotsLeft -= size;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < 0) return ItemStack.EMPTY;
        var slotsLeft = slot;
        for (var inventory : inventories) {
            var size = inventory.size();
            if (slotsLeft < size)
                return inventory.removeStack(slotsLeft, amount);
            slotsLeft -= size;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot < 0) return ItemStack.EMPTY;
        var slotsLeft = slot;
        for (var inventory : inventories) {
            var size = inventory.size();
            if (slotsLeft < size)
                return inventory.removeStack(slotsLeft);
            slotsLeft -= size;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        var slotsLeft = slot;
        if (slotsLeft < 0) return;
        for (var inventory : inventories) {
            var size = inventory.size();
            if (slotsLeft < size) {
                inventory.setStack(slotsLeft, stack);
                return;
            }
            slotsLeft -= size;
        }
    }

    @Override
    public void markDirty() {
        for (var inventory : inventories)
            inventory.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        for (var inventory : inventories)
            if (!inventory.canPlayerUse(player)) return false;
        return true;
    }

    @Override
    public void clear() {
        for (var inventory : inventories)
            inventory.clear();
    }
}
