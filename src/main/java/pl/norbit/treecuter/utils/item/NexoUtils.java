package pl.norbit.treecuter.utils.item;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class NexoUtils {
    private NexoUtils() {}

    public static boolean isEqualBlock(Block b, String id) {
        CustomBlockMechanic customBlock = NexoBlocks.customBlockMechanic(b);

        if(customBlock == null){
            return false;
        }

        return id.equals(customBlock.getItemID());
    }

    /**
     * Check if ItemStack is equal to ItemsAdder item
     * @param item ItemStack
     * @param id Item id
     * @return True if ItemStack is equal to ItemsAdder item
     */
    public static boolean isEqualItem(ItemStack item, String id) {
        String itemId = NexoItems.idFromItem(item);

        if(itemId == null){
            return false;
        }

        return id.equals(itemId);
    }
}
