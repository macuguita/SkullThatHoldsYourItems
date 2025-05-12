package archives.tater.holdingskull;

import com.mojang.serialization.Codec;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class HoldingSkullUtil {
    private HoldingSkullUtil() {}

    public static final Codec<DefaultedList<ItemStack>> DEFAULTED_STACK_LIST_CODEC = ContainerComponent.CODEC.xmap(
            container -> {
                var list = DefaultedList.ofSize(container.);
            }
    )
}
