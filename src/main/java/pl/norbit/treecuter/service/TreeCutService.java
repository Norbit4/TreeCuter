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
import pl.norbit.treecuter.model.BreakTask;
import pl.norbit.treecuter.utils.BlockUtils;
import pl.norbit.treecuter.utils.GlowUtils;
import pl.norbit.treecuter.utils.DurabilityUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static pl.norbit.treecuter.utils.TaskUtils.*;

public class TreeCutService {

    private static final Map<UUID, List<Block>> selectedBlocks = new HashMap<>();
    private static final Map<UUID, Block> mainBlocks = new HashMap<>();
    private static final PluginManager pluginManager = TreeCuter.getInstance().getServer().getPluginManager();
    private static final List<BreakTask> breakTasks = new CopyOnWriteArrayList<>();

    private TreeCutService() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static void start(){
        timer(() -> {
            List<BreakTask> toRemove = new ArrayList<>();

            for (BreakTask breakTree : breakTasks) {
                if (breakTree.isTreeBroken()) {
                    breakTree.leafTask();
                    toRemove.add(breakTree);
                    continue;
                }

                List<Block> blocks = breakTree.getBlocksToBreak();
                blocks.forEach(b -> breakBlock(breakTree.getPlayer(), b));
            }

            breakTasks.removeAll(toRemove);
        }, 2L);
    }

    /**
     * Get selected blocks (to cut) by player
     * @param p Player
     * @return List of blocks, if player has no selected blocks, return empty set
     */
    public static List<Block> getSelectedBlocks(Player p){
        List<Block> blocks = selectedBlocks.get(p.getUniqueId());

        if(blocks == null){
            return new ArrayList<>();
        }

        return blocks;
    }

    /**
     * Remove selected blocks from player and remove glowing effect for blocks
     * @param p Player
     */
    public static void removeColorFromTree(Player p){
        List<Block> blocks = selectedBlocks.get(p.getUniqueId());

        if(blocks == null){
            return;
        }

        GlowUtils.unsetGlowing(blocks, p);
        selectedBlocks.remove(p.getUniqueId());
    }

    /**
     * Select tree to player and apply glowing effect to blocks
     * @param b Block
     * @param p Player
     * @param color Color of glowing effect
     * @param item Item in player hand
     */
    public static void selectTreeByBlock(Block b, Player p, ChatColor color, ItemStack item){
        if (p == null || b == null){
            return;
        }
        //check max uses of player item
        int maxBlock = DurabilityUtils.checkRemainingUses(item);

        List<Block> blocks = BlockUtils.getWoodBlocksAround(new ArrayList<>(), b, maxBlock);

        UUID playerUUID = p.getUniqueId();

        if(blocks.size() < Settings.getMinBlocks()){
            selectedBlocks.remove(playerUUID);
            return;
        }
        //apply mining effect to player
        EffectService.applyEffect(p);

        var treeGlowEvent = new TreeGlowEvent(blocks, p);
        pluginManager.callEvent(treeGlowEvent);

        if(treeGlowEvent.isCancelled()){
            return;
        }

        if(lastBlocksIsSame(playerUUID, b, blocks)){
            return;
        }

        GlowUtils.setGlowing(blocks, p, color);
        selectedBlocks.put(p.getUniqueId(), blocks);
        mainBlocks.put(p.getUniqueId(), b);
    }

    private static boolean lastBlocksIsSame(UUID playerUUID, Block mainBlock, List<Block> blocks){
        List<Block> lastBlocks = selectedBlocks.get(playerUUID );
        Block lastMainBlock = mainBlocks.get(playerUUID);

        if(lastBlocks != null && lastMainBlock != null){
            int lastBlocksSize = lastBlocks.size();
            int blocksSize = blocks.size();

            return lastBlocksSize == blocksSize && lastMainBlock.equals(mainBlock);
        }
        return false;
    }

    /**
     * Cut selected tree by player, when player cut tree, remove glowing effect from blocks and break blocks.
     * When player has no selected blocks, do nothing.
     * @param p Player
     */
    public static void cutTree(Player p) {
        if(p == null){
            return;
        }

        if (!EffectService.isEffectPlayer(p)){
            return;
        }

        List<Block> blocks = selectedBlocks.get(p.getUniqueId());

        if(blocks == null){
            return;
        }

        var treeCutEvent = new TreeCutEvent(blocks, p);

        pluginManager.callEvent(treeCutEvent);

        if(treeCutEvent.isCancelled()){
            return;
        }
        GlowUtils.unsetGlowing(blocks, p);

        //create task to break blocks
        BreakTask breakTask = new BreakTask(blocks, p);
        breakTasks.add(breakTask);

        int size = blocks.size();
        updateItem(p, size - 1);

        selectedBlocks.remove(p.getUniqueId());
    }

    private static void breakBlock(Player p, Block b){
        if(b == null){
            return;
        }

        if(!p.isOnline()){
            return;
        }

        if (Settings.isItemsToInventory()) {
            Material material = b.getType();

            if(material == Material.AIR){
                return;
            }

            p.getInventory().addItem(new ItemStack(material));
            b.setType(Material.AIR);
        } else b.breakNaturally();
    }


    private static void updateItem(Player p, int durabilityDamage){
        PlayerInventory inventory = p.getInventory();

        var itemInHand = inventory.getItemInMainHand();

        if(itemInHand.getType() == Material.AIR){
            return;
        }

        ItemStack itemStack = DurabilityUtils.updateDurability(itemInHand, durabilityDamage);

        inventory.setItemInMainHand(itemStack);
    }
}
