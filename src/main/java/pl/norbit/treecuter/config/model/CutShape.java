package pl.norbit.treecuter.config.model;

import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

@Data
public class CutShape {
    private String id;
    private ChatColor glowingColor;
    private List<CustomItem> acceptTools;

    private CustomTool customTool;
    private List<Material> acceptBlocks;

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

    public boolean isAcceptBlock(Material mat) {
        return acceptBlocks.stream()
                .anyMatch(acceptBlock -> acceptBlock == mat);
    }
}
