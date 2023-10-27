package pl.norbit.treecuter.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.utils.ChatUtils;

public class ItemService {

    public static boolean useTool(ItemStack itemStack){

        if(!Settings.TOOL_ENABLE) return true;

        if(itemStack == null) return false;

        return checkTool(itemStack);
    }

    private static boolean checkTool(ItemStack itemStack) {

        var type = itemStack.getType();

        if(type == Material.AIR) return false;

        String toolMaterial = Settings.TOOL_MATERIAL;

        if (toolMaterial.contains("ia")) {
            if (!Settings.ITEMS_ADDER_IS_ENABLED) throw new RuntimeException("ItemsAdder is not enabled!");

            String[] split = toolMaterial.split(":");

            if(split.length != 2) throw new RuntimeException("Invalid syntax: " + toolMaterial);

            return ItemsAdderService.isEqual(itemStack,  split[1]);
        }

        Material material = Material.getMaterial(toolMaterial);

        if (material == null) throw new RuntimeException("Material " + toolMaterial + " not found!");

        if (material != type) return false;

        var meta = itemStack.getItemMeta();

        if(meta == null) return false;

        if(!meta.hasDisplayName()) return false;

        var displayName = meta.getDisplayName();

        return displayName.equals(ChatUtils.format(Settings.TOOL_NAME));
    }

    public static ItemStack getItem(){
        String toolMaterial = Settings.TOOL_MATERIAL;

        if (toolMaterial.contains("ia")) {
            if (!Settings.ITEMS_ADDER_IS_ENABLED) throw new RuntimeException("ItemsAdder is not enabled!");

            String[] split = toolMaterial.split(":");

            if(split.length != 2) throw new RuntimeException("Invalid syntax: " + toolMaterial);

            ItemStack item = ItemsAdderService.getItem(split[1]);

            if(item == null) throw new RuntimeException("Item " + split[1] + " not found!");

            return item;
        }

        Material material = Material.getMaterial(toolMaterial);

        if (material == null) throw new RuntimeException("Material " + toolMaterial + " not found!");

        ItemStack itemStack = new ItemStack(material, 1);

        var meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatUtils.format(Settings.TOOL_NAME));

        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
