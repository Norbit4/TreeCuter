package pl.norbit.treecuter.utils.item;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ItemsAdderUtils {
    private ItemsAdderUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }


    public static int checkRemainUses(ItemStack itemStack) {
        CustomStack stack = CustomStack.byItemStack(itemStack);

        if(stack == null){
            return -1; // Not an ItemsAdder item
        }

        return stack.getDurability();
    }

    public static ItemStack updateDurability(ItemStack itemStack, int dmg) {
        CustomStack stack = CustomStack.byItemStack(itemStack);

        if(stack == null){
            return null;
        }

        stack.setDurability(stack.getDurability() - dmg);

        return stack.getItemStack();
    }

    /**
     * Get itemsadder item by id
     * @param id Item id
     * @return Optional of ItemStack
     */
    public static Optional<ItemStack> getItem(String id){
        CustomStack stack = CustomStack.getInstance(id);

        if(stack == null){
            return Optional.empty();
        }

        return Optional.of(stack.getItemStack());
    }

    /**
     * Check if ItemStack is equal to ItemsAdder item
     * @param item ItemStack
     * @param id Item id
     * @return True if ItemStack is equal to ItemsAdder item
     */

    public static boolean isEqual(ItemStack item, String id) {
        CustomStack stack = CustomStack.getInstance(id);

        if(stack == null){
            return false;
        }

        CustomStack customStack = CustomStack.byItemStack(item);

        if(customStack == null){
            return false;
        }

        return customStack.matchNamespacedID(stack);
    }
}
