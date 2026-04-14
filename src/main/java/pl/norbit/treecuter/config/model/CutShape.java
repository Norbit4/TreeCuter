package pl.norbit.treecuter.config.model;

import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.norbit.treecuter.utils.item.MaterialMatcherUtils;

import java.util.List;
import java.util.Objects;

@Data
public class CutShape {
    private String id;
    private ChatColor glowingColor;
    private List<CustomItem> acceptTools;

    private CustomTool customTool;
    private List<String> acceptBlocks;

    public ItemStack getCustomToolItem() {
        if (customTool != null) {
            return customTool.getItemStack();
        }
        return null;
    }

    public boolean isAcceptTool(ItemStack item) {
        if(customTool != null){
            ItemStack itemStack = customTool.getItemStack();
            ItemMeta itemMeta = itemStack.getItemMeta();

            if(itemStack.getType() != item.getType()){
                return false;
            }

            return Objects.equals(itemMeta.getDisplayName(), item.getItemMeta().getDisplayName());
        }

        return acceptTools.stream()
                .anyMatch(acceptTool -> acceptTool.isEqual(item));
    }

    public boolean isAcceptBlock(Block b) {
        return acceptBlocks.stream()
                .anyMatch(acceptBlock -> MaterialMatcherUtils.isEqual(b, acceptBlock));
    }
}
