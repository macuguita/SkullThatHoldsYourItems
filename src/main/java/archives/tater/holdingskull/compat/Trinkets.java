package archives.tater.holdingskull.compat;

import eu.pb4.trinkets.api.TrinketDropRule;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.util.Tuple;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class Trinkets {

    private Trinkets() {}

    public static void addTrinketsToSkullGrave(SimpleContainer inventory, Player player) {
        var trinkets = TrinketsApi.getAttachment(player);
        int trinketSlotIndex = player.getInventory().getContainerSize();

        for (Tuple<TrinketSlotAccess, ItemStack> slotReferenceItemStackPair : trinkets.getAllEquipped()) {
            var slotAccess = slotReferenceItemStackPair.getA();
            var stack = slotReferenceItemStackPair.getB();

            if (stack.isEmpty()) continue;

            TrinketDropRule dropRule = TrinketsApi.getDropRule(stack, slotAccess, player, false);

            if (dropRule == TrinketDropRule.DROP) {
                if (trinketSlotIndex < inventory.getContainerSize()) {
                    inventory.setItem(++trinketSlotIndex, stack);
                } else {
                    // Fallback if container is somehow too small
                    inventory.addItem(stack);
                }
                slotAccess.set(ItemStack.EMPTY);
            }
        }
    }
}
