package archives.tater.holdingskull;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CompositeInventory implements Container {
    private final Container[] inventories;

    public CompositeInventory(Container... inventories) {
        this.inventories = inventories;
    }

    @Override
    public int getContainerSize() {
        var size = 0;
        for (var inventory : inventories)
            size += inventory.getContainerSize();
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (var inventory : inventories)
            if (!inventory.isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0) return ItemStack.EMPTY;
        var slotsLeft = slot;
        for (var inventory : inventories) {
            var size = inventory.getContainerSize();
            if (slotsLeft < size)
                return inventory.getItem(slotsLeft);
            slotsLeft -= size;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot < 0) return ItemStack.EMPTY;
        var slotsLeft = slot;
        for (var inventory : inventories) {
            var size = inventory.getContainerSize();
            if (slotsLeft < size)
                return inventory.removeItem(slotsLeft, amount);
            slotsLeft -= size;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0) return ItemStack.EMPTY;
        var slotsLeft = slot;
        for (var inventory : inventories) {
            var size = inventory.getContainerSize();
            if (slotsLeft < size)
                return inventory.removeItemNoUpdate(slotsLeft);
            slotsLeft -= size;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        var slotsLeft = slot;
        if (slotsLeft < 0) return;
        for (var inventory : inventories) {
            var size = inventory.getContainerSize();
            if (slotsLeft < size) {
                inventory.setItem(slotsLeft, stack);
                return;
            }
            slotsLeft -= size;
        }
    }

    @Override
    public void setChanged() {
        for (var inventory : inventories)
            inventory.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        for (var inventory : inventories)
            if (!inventory.stillValid(player)) return false;
        return true;
    }

    @Override
    public void clearContent() {
        for (var inventory : inventories)
            inventory.clearContent();
    }
}
