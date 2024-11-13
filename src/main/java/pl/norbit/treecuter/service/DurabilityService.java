package pl.norbit.treecuter.service;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.norbit.treecuter.utils.DurabilityUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DurabilityService {
//    private static final Map<UUID, Integer> durabilityMap = new HashMap<>();
//
//    private DurabilityService() {
//        throw new IllegalStateException("This class cannot be instantiated");
//    }
//
//    public static int calculateDurability(Player p, int durability){
//        PlayerInventory inv = p.getInventory();
//
//        var itemInHand = inv.getItemInMainHand();
//        int level = itemInHand.getEnchantmentLevel(Enchantment.DURABILITY);
//
//        return getDurabilityByLevel(level, durability);
//    }
//
//    protected static void addDurability(Player p, int durability){
//        UUID playerUUID = p.getUniqueId();
//        PlayerInventory inv = p.getInventory();
//
//        var itemInHand = inv.getItemInMainHand();
//        int level = itemInHand.getEnchantmentLevel(Enchantment.DURABILITY);
//
//        durability = getDurabilityByLevel(level, durability);
//
//        durabilityMap.put(playerUUID, durability);
//    }
//
//    private static int getDurabilityByLevel(int level, int durability){
//        return (int) Math.ceil(durability / (level + 1));
//    }
//
//    protected static void updateItem(Player p){
//        PlayerInventory inv = p.getInventory();
//
//        var itemInHand = inv.getItemInMainHand();
//
//        if(itemInHand.getType() == Material.AIR){
//            return;
//        }
//
//        UUID playerUUID = p.getUniqueId();
//
//        int durabilityDamage = durabilityMap.get(playerUUID);
//
//        if(durabilityDamage == 0){
//            return;
//        }
//
//        ItemStack itemStack = DurabilityUtils.updateDurability(itemInHand, durabilityDamage);
//
//        inv.setItemInMainHand(itemStack);
//    }
}
