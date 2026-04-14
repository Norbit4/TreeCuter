package pl.norbit.treecuter.utils.item;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import pl.norbit.treecuter.config.model.ItemType;

public class MaterialMatcherUtils {
    private MaterialMatcherUtils (){}

    public static boolean isEqual(ItemStack itemStack, String materialId) {
        if (itemStack == null) return false;
        return match(materialId, itemStack.getType(), itemStack, null);
    }

    public static boolean isEqual(Block block, String materialId) {
        if (block == null) return false;
        return match(materialId, block.getType(), null, block);
    }

    private static boolean match(String materialId, Material material, ItemStack itemStack, Block block) {
        String[] split = materialId.split(":");

        // VANILLA np. "STONE"
        if (split.length < 2) {
            Material mat = Material.getMaterial(materialId.toUpperCase());
            return mat != null && material == mat;
        }

        String namespace = split[0];
        String id = split[1];

        ItemType type = getType(namespace);

        return switch (type) {
            case ITEMS_ADDER -> {
                if (itemStack != null) {
                    yield ItemsAdderUtils.isEqualItem(itemStack, id);
                }
                if (block != null) {
                    yield ItemsAdderUtils.isEqualBlock(block, id);
                }
                yield false;
            }
            case NEXO -> {
                if (itemStack != null) {
                    yield NexoUtils.isEqualItem(itemStack, id);
                }
                if (block != null) {
                    yield NexoUtils.isEqualBlock(block, id);
                }
                yield false;
            }
            case MINECRAFT -> {
                Material mat = Material.getMaterial(id.toUpperCase());
                yield mat != null && material == mat;
            }
        };
    }

    private static ItemType getType(String namespace) {
        if (namespace.equalsIgnoreCase("ia")) {
            return ItemType.ITEMS_ADDER;
        } else if (namespace.equalsIgnoreCase("nexo")) {
            return ItemType.NEXO;
        }

        return ItemType.MINECRAFT;
    }
}