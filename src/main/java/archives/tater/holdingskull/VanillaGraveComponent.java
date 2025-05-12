package archives.tater.holdingskull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import static archives.tater.holdingskull.HoldingSkullUtil.DEFAULTED_STACK_LIST_CODEC;

public record VanillaGraveComponent(
        DefaultedList<ItemStack> main,
        DefaultedList<ItemStack> armor,
        DefaultedList<ItemStack> offHand
) implements GraveComponent {

    @Override
    public Inventory asInventory() {
        return new CompositeInventory(
                new ListBackedInventory(main),
                new ListBackedInventory(armor),
                new ListBackedInventory(offHand)
        );
    }

    public boolean isEmpty() {
        return main.isEmpty() && armor.isEmpty() && offHand.isEmpty();
    }

    public static final Codec<VanillaGraveComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DEFAULTED_STACK_LIST_CODEC.fieldOf("main").forGetter(VanillaGraveComponent::main),
            DEFAULTED_STACK_LIST_CODEC.fieldOf("armor").forGetter(VanillaGraveComponent::armor),
            DEFAULTED_STACK_LIST_CODEC.fieldOf("offHand").forGetter(VanillaGraveComponent::offHand)
    ).apply(instance, VanillaGraveComponent::new));

    public static VanillaGraveComponent of(PlayerInventory playerInventory) {
        return new VanillaGraveComponent(playerInventory.main,  playerInventory.armor, playerInventory.offHand);
    }

    public static VanillaGraveComponent empty() {
        return new VanillaGraveComponent(
                DefaultedList.ofSize(PlayerInventory.MAIN_SIZE, ItemStack.EMPTY),
                DefaultedList.ofSize(PlayerInventory.ARMOR_SLOTS.length, ItemStack.EMPTY),
                DefaultedList.ofSize(1, ItemStack.EMPTY)
        );
    }
}
