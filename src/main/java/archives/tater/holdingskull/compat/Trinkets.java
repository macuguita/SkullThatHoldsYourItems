package archives.tater.holdingskull.compat;

import eu.pb4.trinkets.api.TrinketDropRule;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.util.Tuple;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class Trinkets {

    private Trinkets() {}

    public static void addTrinketsToSkullGrave(SimpleContainer inventory, Player player, boolean keepInv) {
        var trinkets = TrinketsApi.getAttachment(player);
        for (Tuple<TrinketSlotAccess, ItemStack> slotReferenceItemStackPair : trinkets.getAllEquipped()) {
            var slotAccess = slotReferenceItemStackPair.getA();
            var stack = slotReferenceItemStackPair.getB();

            TrinketDropRule dropRule = TrinketsApi.getDropRule(stack, slotAccess, player, keepInv);

            if (dropRule == TrinketDropRule.DROP || dropRule == TrinketDropRule.DEFAULT) {
                inventory.addItem(stack);
            }
        }
    }

    public static void clearTrinketsInventory(LivingEntity entity) {
        var trinkets = TrinketsApi.getAttachment(entity);
        for (Tuple<TrinketSlotAccess, ItemStack> slotAccessItemStackTuple : trinkets.getAllEquipped()) {
            var slotAccess = slotAccessItemStackTuple.getA();
            slotAccess.inventory().setItem(slotAccess.index(), ItemStack.EMPTY);
        }
    }
}
