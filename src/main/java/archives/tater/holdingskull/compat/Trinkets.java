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
        for (Tuple<TrinketSlotAccess, ItemStack> slotReferenceItemStackPair : trinkets.getAllEquipped()) {
            var slotAccess = slotReferenceItemStackPair.getA();
            var stack = slotReferenceItemStackPair.getB();

            if (stack.isEmpty()) {
                continue;
            }

            // if we are running this, keep inventory is off because if it was the grave wouldn't spawn
            TrinketDropRule dropRule = TrinketsApi.getDropRule(stack, slotAccess, player, false);

            if (dropRule == TrinketDropRule.DROP) {
                inventory.addItem(stack);
                slotAccess.set(ItemStack.EMPTY);
            }
        }
    }
}
