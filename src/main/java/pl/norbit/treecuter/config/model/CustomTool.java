package pl.norbit.treecuter.config.model;

import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.norbit.treecuter.utils.ChatUtils;

import java.util.List;

@Data
public class CustomTool {
    private String name;
    private String material;
    private List<String> lore;

    public ItemStack getItemStack() {
        Material mat = Material.getMaterial(material.toUpperCase());

        if (mat == null) {
            throw new IllegalArgumentException("Invalid material: " + material);
        }

        ItemStack itemStack = new ItemStack(mat);

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> formatLore = lore.stream()
                .map(ChatUtils::format)
                .toList();

        itemMeta.setDisplayName(ChatUtils.format(name));
        itemMeta.setLore(formatLore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
