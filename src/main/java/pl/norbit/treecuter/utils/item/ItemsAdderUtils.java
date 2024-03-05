package pl.norbit.treecuter.utils.item;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ItemsAdderUtils {
    private ItemsAdderUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    /**
     * Get itemsadder item by id
     * @param id Item id
     * @return Optional of ItemStack
     */
    protected static Optional<ItemStack> getItem(String id){
        CustomStack stack = CustomStack.getInstance(id);

        if(stack == null) return Optional.empty();

        return Optional.of(stack.getItemStack());
    }

    /**
     * Check if ItemStack is equal to ItemsAdder item
     * @param item ItemStack
     * @param id Item id
     * @return True if ItemStack is equal to ItemsAdder item
     */

    protected static boolean isEqual(ItemStack item, String id) {
        CustomStack stack = CustomStack.getInstance(id);

        if(stack == null) return false;

        CustomStack customStack = CustomStack.byItemStack(item);

        if(customStack == null) return false;

        return customStack.matchNamespacedID(stack);
    }
}
