package pl.norbit.treecuter.service;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.api.listeners.TreeCutEvent;
import pl.norbit.treecuter.api.listeners.TreeGlowEvent;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.utils.BlockUtils;
import pl.norbit.treecuter.utils.GlowUtils;
import pl.norbit.treecuter.utils.DurabilityUtils;

import java.util.*;

public class TreeCutService {
    private static final Map<UUID, Set<Block>> SELECTED_BLOCKS = new HashMap<>();
    private static final PluginManager pluginManager = TreeCuter.getInstance().getServer().getPluginManager();

    private TreeCutService() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    /**
     * Get selected blocks (to cut) by player
     * @param p Player
     * @return Set of blocks, if player has no selected blocks, return empty set
     */
    public static Set<Block> getSelectedBlocks(Player p){
        Set<Block> blocks = SELECTED_BLOCKS.get(p.getUniqueId());

        if(blocks == null) return new HashSet<>();

        return blocks;
    }

    /**
     * Remove selected blocks from player and remove glowing effect for blocks
     * @param p Player
     */
    public static void removeColorFromTree(Player p){
        Set<Block> blocks = SELECTED_BLOCKS.get(p.getUniqueId());

        if(blocks == null) return;

        GlowUtils.unsetGlowing(blocks, p);
        SELECTED_BLOCKS.remove(p.getUniqueId());
    }

    /**
     * Select tree to player and apply glowing effect to blocks
     * @param b Block
     * @param p Player
     * @param color Color of glowing effect
     * @param item Item in player hand
     */
    public static void selectTreeByBlock(Block b, Player p, ChatColor color, ItemStack item){
        //check max uses of player item
        int maxBlock = DurabilityUtils.checkRemainingUses(item);

        Set<Block> blocks = BlockUtils.getBlocksAround(new HashSet<>(), b, maxBlock);
        blocks.add(b);

        if(blocks.size() < Settings.MIN_BLOCKS){
            SELECTED_BLOCKS.remove(p.getUniqueId());
            return;
        }
        //apply mining effect to player
        EffectService.applyEffect(p);

        var treeGlowEvent = new TreeGlowEvent(blocks, p);
        pluginManager.callEvent(treeGlowEvent);

        if(treeGlowEvent.isCancelled()) return;

        GlowUtils.setGlowing(blocks, p, color);
        SELECTED_BLOCKS.put(p.getUniqueId(), blocks);
    }

    /**
     * Cut selected tree by player, when player cut tree, remove glowing effect from blocks and break blocks.
     * When player has no selected blocks, do nothing.
     * @param p Player
     */
    public static void cutTree(Player p) {
        if (!EffectService.isEffectPlayer(p)) return;

        Set<Block> blocks = SELECTED_BLOCKS.get(p.getUniqueId());

        if(blocks == null) return;

        var treeCutEvent = new TreeCutEvent(blocks, p);

        pluginManager.callEvent(treeCutEvent);

        if(treeCutEvent.isCancelled()) return;

        GlowUtils.unsetGlowing(blocks, p);

        blocks.forEach(block -> breakBlock(p, block));

        updateItem(p, blocks.size());

        SELECTED_BLOCKS.remove(p.getUniqueId());
    }

    private static void breakBlock(Player p, Block b){
        if (Settings.ITEMS_TO_INVENTORY) {
            Material material = b.getType();

            if(material == Material.AIR) return;

            p.getInventory().addItem(new ItemStack(material));
            b.setType(Material.AIR);
        } else b.breakNaturally();
    }


    private static void updateItem(Player p, int durabilityDamage){
        PlayerInventory inventory = p.getInventory();

        var itemInHand = inventory.getItemInMainHand();

        if(itemInHand.getType() == Material.AIR) return;

        ItemStack itemStack = DurabilityUtils.updateDurability(itemInHand, durabilityDamage);

        inventory.setItemInMainHand(itemStack);
    }
}
