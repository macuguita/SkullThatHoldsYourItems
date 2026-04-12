package archives.tater.holdingskull;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.Optional;
import java.util.UUID;

public class SkullGraveMenu extends ChestMenu {

    private final SkullGraveData data;

    public SkullGraveMenu(int containerId, Inventory inventory, Container skull, SkullGraveData data) {
        super(MenuType.GENERIC_9x5, containerId, inventory, skull, 5);
        this.data = data;
    }

    public SkullGraveMenu(int containerId, Inventory inventory, SkullGraveData owner) {
        this(containerId, inventory, new SimpleContainer(45), owner);
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

    public record SkullGraveData(Optional<UUID> owner, boolean isUnclaimed) {
        public static final StreamCodec<ByteBuf, SkullGraveData> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional),
                SkullGraveData::owner,
                ByteBufCodecs.BOOL,
                SkullGraveData::isUnclaimed,
                SkullGraveData::new
        );
    }
}
