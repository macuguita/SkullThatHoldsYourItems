package archives.tater.holdingskull;

import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public class SkullGraveSlot extends Slot {

    private final SkullGraveMenu menu;

    public SkullGraveSlot(SkullGraveMenu menu, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.menu = menu;
    }

    @Override
    public boolean mayPickup(Player player) {
        return menu.getOwner().map(owner -> owner.equals(player.getUUID())).orElse(false)
                || menu.isUnclaimed()
                || (HoldingSkull.CONFIG.opsCanBypassUnclaimedSkulls && player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER));
    }
}
