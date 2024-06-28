package pl.norbit.treecuter.utils.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.exception.ItemAdderException;
import pl.norbit.treecuter.exception.MinecraftMaterialException;
import pl.norbit.treecuter.utils.ChatUtils;

public class ItemsUtils {

    private ItemsUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static boolean useTool(ItemStack itemStack){

        if(!Settings.isToolEnable()){
            return true;
        }

        if(itemStack == null){
            return false;
        }

        return checkTool(itemStack);
    }

    private static boolean checkTool(ItemStack itemStack) {
        var type = itemStack.getType();

        if(type == Material.AIR){
            return false;
        }

        String toolMaterial = Settings.getToolMaterial();

        if (toolMaterial.contains("ia")) {
            if (!Settings.isItemsAdderEnabled()){
                throw new ItemAdderException("ItemsAdder is not enabled!");
            }

            String[] split = toolMaterial.split(":");

            if(split.length != 2){
                throw new ItemAdderException("Invalid syntax: " + toolMaterial);
            }

            return ItemsAdderUtils.isEqual(itemStack,  split[1]);
        }

        Material material = Material.getMaterial(toolMaterial);

        if (material == null){
            throw new MinecraftMaterialException("Material " + toolMaterial + " not found!");
        }

        if (material != type){
            return false;
        }

        var meta = itemStack.getItemMeta();

        if(meta == null){
            return false;
        }

        if(!meta.hasDisplayName()){
            return false;
        }

        var displayName = meta.getDisplayName();

        return displayName.equals(ChatUtils.format(Settings.getToolName()));
    }

    public static ItemStack getItem(){
        String toolMaterial = Settings.getToolMaterial();

        if (toolMaterial.contains("ia")) {
            if (!Settings.isItemsAdderEnabled()){
                throw new ItemAdderException("ItemsAdder is not enabled!");
            }

            String[] split = toolMaterial.split(":");

            if(split.length != 2){
                throw new ItemAdderException("Invalid syntax: " + toolMaterial);
            }

           return ItemsAdderUtils.getItem(split[1])
                   .orElseThrow(() -> new ItemAdderException("Item " + split[1] + " not found!"));
        }

        Material material = Material.getMaterial(toolMaterial);

        if (material == null){
            throw new MinecraftMaterialException("Material " + toolMaterial + " not found!");
        }

        ItemStack itemStack = new ItemStack(material, 1);

        var meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatUtils.format(Settings.getToolName()));

        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
