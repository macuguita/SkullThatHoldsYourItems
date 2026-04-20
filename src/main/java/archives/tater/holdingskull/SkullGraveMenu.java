package archives.tater.holdingskull;

import archives.tater.holdingskull.compat.CompatHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class SkullGraveMenu extends ChestMenu {

    private final SkullGraveData data;
    private final Container skullInventory;

    public SkullGraveMenu(int containerId, Inventory inventory, Container skullInventory, SkullGraveData data) {
        var menuType = CompatHelper.isTrinketsLoaded() ? MenuType.GENERIC_9x6 : MenuType.GENERIC_9x5;
        var rows = CompatHelper.isTrinketsLoaded() ? 6 : 5;
        super(menuType, containerId, inventory, skullInventory, rows);
        this.data = data;
        this.skullInventory = skullInventory;
    }

    public SkullGraveMenu(int containerId, Inventory inventory, SkullGraveData owner) {
        var invSize = CompatHelper.isTrinketsLoaded() ? 6 * 9 : 5 * 9;
        this(containerId, inventory, new SimpleContainer(invSize), owner);
    }

    public Optional<UUID> getOwner() {
        return data.owner;
    }

    public boolean isUnclaimed() {
        return data.isUnclaimed;
    }

    @Override
    protected void addChestGrid(final Container container, final int left, final int top) {
        for (int y = 0; y < this.getRowCount(); y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new SkullGraveSlot(this, container, x + y * 9, left + x * 18, top + y * 18));
            }
        }
    }

    @Override
    public MenuType<?> getType() {
        return HoldingSkull.SKULL_GRAVE_MENU;
    }

    @Nullable
    private Entity getSkullGrave(Level level) {
        return level.getEntity(data.skullId);
    }

    @Override
    public boolean stillValid(Player player) {
        var skullGrave = getSkullGrave(player.level());
        if (skullGrave != null) {
            return skullGrave.isAlive()
                    && this.skullInventory.stillValid(player)
                    && player.isWithinEntityInteractionRange(skullGrave, 4.0);
        }
        return false;
    }

    @Override
    public void removed(final Player player) {
        super.removed(player);
        this.skullInventory.stopOpen(player);
    }

    public record SkullGraveData(Integer skullId, Optional<UUID> owner, boolean isUnclaimed) {
        public static final StreamCodec<ByteBuf, SkullGraveData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                SkullGraveData::skullId,
                UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional),
                SkullGraveData::owner,
                ByteBufCodecs.BOOL,
                SkullGraveData::isUnclaimed,
                SkullGraveData::new
        );
    }
}
