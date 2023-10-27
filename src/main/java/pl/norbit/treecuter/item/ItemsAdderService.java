package pl.norbit.treecuter.item;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderService {
    public static ItemStack getItem(String id){
        CustomStack stack = CustomStack.getInstance(id);

        if(stack == null) return null;

        return stack.getItemStack();
    }

    public static boolean isEqual(ItemStack stack1, String id) {
        CustomStack stack = CustomStack.getInstance(id);

        if(stack == null) return false;

        CustomStack customStack = CustomStack.byItemStack(stack1);

        if(customStack == null) return false;

        return customStack.matchNamespacedID(stack);
    }
}
