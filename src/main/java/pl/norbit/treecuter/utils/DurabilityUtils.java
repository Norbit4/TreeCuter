package pl.norbit.treecuter.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import pl.norbit.treecuter.config.Settings;

public class DurabilityUtils {
    private DurabilityUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static int checkRemainingUses(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        
        if(meta.isUnbreakable()){
            return Settings.getMaxBlocks();
        }
        
//        if(Settings.isItemsAdderEnabled()){
//            int uses = ItemsAdderUtils.checkRemainUses(item);
//
//            if(uses != -1){
//                return uses;
//            }
//        }
        
        if (meta instanceof Damageable damageable) {
            int maxDurability = item.getType().getMaxDurability();
            int currentDamage = damageable.getDamage();
            int remainingDurability = maxDurability - currentDamage;

            //Check for durability enchantment
            int unbreakingLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
            if (unbreakingLevel > 0) return remainingDurability * (unbreakingLevel + 1);
            
            return remainingDurability;
        }
        return 0;
    }

    public static ItemStack updateDurability(ItemStack item, int dmg){
        ItemMeta meta = item.getItemMeta();
        
        if(meta.isUnbreakable()){
            return item;
        }
        
//        if(Settings.isItemsAdderEnabled()){
//            ItemStack itemStack = ItemsAdderUtils.updateDurability(item, dmg);
//
//            if(itemStack != null){
//                return item;
//            }
//        }
        
        if (meta instanceof Damageable damageable){
            int maxDurability = item.getType().getMaxDurability();

            //Check for durability enchantment
            int unbreakingLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
            int actualDamage = dmg;
            if (unbreakingLevel > 0) actualDamage = dmg / (unbreakingLevel + 1);

            if(damageable.getDamage() + actualDamage >= maxDurability){
                return null;
            }
            damageable.setDamage((damageable.getDamage() + actualDamage));
        }
        item.setItemMeta(meta);
        return item;
    }
}
