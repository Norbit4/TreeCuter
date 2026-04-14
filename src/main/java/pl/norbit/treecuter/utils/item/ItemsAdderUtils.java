package pl.norbit.treecuter.utils.item;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderUtils {
    private ItemsAdderUtils() {}

    public static void iaBreak(Block b){
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(b);

        if(customBlock != null){
            CustomBlock.remove(b.getLocation());
        }else {
            b.breakNaturally();
        }
    }

    public static boolean isEqualBlock(Block b, String id) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(b);

        if(customBlock == null){
            return false;
        }
        return id.equals(customBlock.getId());
    }

    /**
     * Check if ItemStack is equal to ItemsAdder item
     * @param item ItemStack
     * @param id Item id
     * @return True if ItemStack is equal to ItemsAdder item
     */
    public static boolean isEqualItem(ItemStack item, String id) {
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
